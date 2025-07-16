document.addEventListener('DOMContentLoaded', () => {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
        window.location.href = '/login';
        return;
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${accessToken}`
    };

    const productList = document.getElementById('product-list');
    const productPagination = document.getElementById('product-pagination');
    const memberList = document.getElementById('member-list');
    const logoutButton = document.getElementById('logout-button');
    
    const productForm = document.getElementById('product-form');
    const productIdInput = document.getElementById('product-id');
    const productNameInput = document.getElementById('product-name');
    const productPriceInput = document.getElementById('product-price');
    const productImageUrlInput = document.getElementById('product-imageUrl');
    const clearFormButton = document.getElementById('clear-form-button');


    let currentProductPage = 0;

    const fetchProducts = async (page = 0) => {
        try {
            const response = await fetch(`/api/products?page=${page}&size=5`, { headers });
            if(response.status === 401) {
                 localStorage.removeItem('accessToken');
                 window.location.href = '/login';
                 return;
            }
            const data = await response.json();
            renderProducts(data.content);
            renderPagination(productPagination, data, fetchProducts);
            currentProductPage = page;
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    };

    const fetchMembers = async () => {
        try {
            const response = await fetch('/api/admin/members', { headers });
            if(response.status === 401) {
                 localStorage.removeItem('accessToken');
                 window.location.href = '/login';
                 return;
            }
             if(response.status === 403) {
                memberList.innerHTML = '<p>회원 목록을 볼 권한이 없습니다.</p>';
                return;
            }
            const data = await response.json();
            renderMembers(data);
        } catch (error) {
            console.error('Error fetching members:', error);
        }
    };

    const renderProducts = (products) => {
        productList.innerHTML = '';
        products.forEach(product => {
            const item = document.createElement('div');
            item.className = 'product-item';
            item.innerHTML = `
                <div>
                    <strong>${product.name}</strong> - ${product.price}원
                </div>
                <div>
                    <button onclick="editProduct(${product.id}, '${product.name}', ${product.price}, '${product.imageUrl}')">수정</button>
                    <button onclick="deleteProduct(${product.id})">삭제</button>
                </div>
            `;
            productList.appendChild(item);
        });
    };
    
    const renderMembers = (members) => {
        memberList.innerHTML = '';
        members.forEach(member => {
            const item = document.createElement('div');
            item.className = 'member-item';
            item.innerHTML = `
                <div>
                    <strong>${member.email}</strong> (${member.role})
                </div>
            `;
            memberList.appendChild(item);
        });
    };

    /*
    const renderPagination = (container, pageData, fetchFunction) => {
        container.innerHTML = '';
        if (pageData.totalPages > 1) {
            const currentPage = pageData.number || 0;
            if (!pageData.first) {
                const prevButton = document.createElement('button');
                prevButton.innerText = '이전';
                prevButton.onclick = () => fetchFunction(currentPage - 1);
                container.appendChild(prevButton);
            }
            if (!pageData.last) {
                const nextButton = document.createElement('button');
                nextButton.innerText = '다음';
                nextButton.onclick = () => fetchFunction(currentPage + 1);
                container.appendChild(nextButton);
            }
        }
    };
    */
    
    productForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = productIdInput.value;
        const productData = {
            name: productNameInput.value,
            price: parseInt(productPriceInput.value),
            imageUrl: productImageUrlInput.value
        };

        const url = id ? `/api/products/${id}` : '/api/products';
        const method = id ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, { method, headers, body: JSON.stringify(productData) });
            if (response.ok) {
                alert('상품이 저장되었습니다.');
                clearForm();
                fetchProducts(currentProductPage);
            } else {
                alert('저장 실패');
            }
        } catch (error) {
            console.error('Error saving product:', error);
        }
    });
    
    const clearForm = () => {
        productIdInput.value = '';
        productNameInput.value = '';
        productPriceInput.value = '';
        productImageUrlInput.value = '';
    }
    
    clearFormButton.addEventListener('click', clearForm);

    window.editProduct = (id, name, price, imageUrl) => {
        productIdInput.value = id;
        productNameInput.value = name;
        productPriceInput.value = price;
        productImageUrlInput.value = imageUrl;
        window.scrollTo(0, 0);
    };

    window.deleteProduct = async (id) => {
        if (!confirm('정말로 삭제하시겠습니까?')) return;
        try {
            const response = await fetch(`/api/products/${id}`, { method: 'DELETE', headers });
            if (response.ok) {
                alert('삭제되었습니다.');
                fetchProducts(currentProductPage);
            } else {
                alert('삭제 실패');
            }
        } catch (error) {
            console.error('Error deleting product:', error);
        }
    };
    
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
    });

    fetchProducts();
    fetchMembers();
});
 