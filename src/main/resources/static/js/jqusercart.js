
let isLoggedIn = false;
let cart = [];
let myproducts = [];
function submitOrder() {
    if (!isLoggedIn) {
        alert("請先登入！");
        return;
    }

    const username = $('#username').val();
    const total = cart.reduce((sum, item) => sum + item.price*item.quantity, 0);
    console.log('total:' + total);
    const order = {
        username: username,
        totalPrice: total,
        items: cart.map(p => ({
            pid: p.id,
            productTitle: p.title,
            productPrice: p.price,
            quantity:p.quantity
        }))
    };
    console.log("orders list :" + JSON.stringify(order));

    $.ajax({
        url: "http://localhost:8080/api/orders",
        type: "POST",
        contentType: "application/json",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        data: JSON.stringify(order),
        success: function (data) {
            alert("訂單已送出！");
            cart = [];
            updateCart();
        }
    });
}

function updateCart() {
    $('#cartItems').empty();
    let total = 0;

    if (cart.length === 0) {
        $('#cartItems').append('<li class="list-group-item">購物車是空的</li>');
    } else {
        cart.forEach((item, index) => {
            total += item.price*item.quantity;
            $('#cartItems').append(`
          <li class="list-group-item d-flex justify-content-between align-items-center">
            ${item.title} - ${item.price} 元 ,數量: ${item.quantity}
            <button class="btn btn-sm btn-danger" onclick="removeFromCart(${index})">刪除</button>
          </li>
        `);
        });
    }
    $('#totalPrice').text(total);
}

function start() {   
	isLoggedIn=false;
    $('.nav-link').click(function (e) {
        e.preventDefault();
        let target = $(this).data('target');
        $('#content > div').removeClass('active');
        $('#' + target).addClass('active');

        if (target === 'products') loadProducts();
        if (target === 'cart') updateCart();
        if (target === 'orders') showOrders();
    });

    $('#loginBtn').click(function (e) {
        e.preventDefault();
        const user = $('#username').val();
        const pass = $('#password').val();

        $.ajax({
            url: "http://localhost:8080/api/user/login",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ username: user, password: pass }),
            success: function (res) {
                localStorage.setItem("token", res.token);
                console.log("encoded data :" + res.token);
                isLoggedIn = true;
                $('#loginMessage').text('');
                $('#loginStatus').text(`歡迎，${user}`);
                sessionStorage.setItem("username",`${user}`);
                alert("登入成功！");
            },
            error: function (xhr) {
                $('#loginMessage').text('帳號或密碼錯誤');
            }
        });

    });    
    function loadProducts() {
        $('#productList').empty();
        $.ajax({
            url: "http://localhost:8080/api/products",
            type: "GET",
            dataType: "json",
            success: function (products) {
                myproducts = products;
                $.each(products, function (i, product) {
                    $('#productList').append(`
                               <div class="col-md-3">
                                 <div class="card mb-3">
                                  <div class="card-body">
                                    <h5 class="card-title">${product.title}</h5>
                                    <img src="${product.image}"  class="card-img-top" width="160" height="200"/>
                                    <p class="card-text">價格：${product.price} 元</p>
                                    <p class="card-text">購買數量：<input type="text" id=qty${i} value="1"></p>
                           <button class="btn btn-success" onclick="addToCart(${product.id},qty${i})">加入購物車</button>
                                 </div>
                                </div>
                              </div>
                            `);
                });
            },
            error: function (xhr) {
                $('#loginMessage').text('帳號或密碼錯誤');
            }
        });

    }
}
function showOrders() {
    if(sessionStorage.getItem("username")==null){
        alert('user not login');
        return;
    }
    $('#orderList').empty();
    $('#itemList').empty();
    $('#orderList').append(`
        <div class="col-md-3">                            
            <p>訂單編號</p>                                                      
         </div>
         <div class="col-md-3">                            
            <p>訂單用戶</p>                                                      
         </div>
         <div class="col-md-3">                            
            <p>訂單時間</p>                                                      
         </div>
        <div class="col-md-3">                            
          <p>Action</p>
         </div>
    `);
    $.ajax({
        url: "http://localhost:8080/api/orders/"+sessionStorage.getItem("username"),
        type: "GET",
        dataType: "json",
        success: function (orders) {           
            $.each(orders, function (i, order) {
                $('#orderList').append(`
                            <div class="col-md-3">                            
                                <p>${order.id}</p>                                                      
                             </div>
                             <div class="col-md-3">                            
                                <p>${order.username}</p>                                                      
                             </div>
                             <div class="col-md-3">                            
                                <p>${order.orderTime}</p>                                                      
                             </div>
                            <div class="col-md-3">                            
                              <button class="btn btn-success" onclick="showDetails(${order.id})">顯示定購商品</button>
                             </div>
                        `);
            });
        },
        error: function (xhr) {
            $('#loginMessage').text('帳號或密碼錯誤');
        }
    });

}
function showDetails(orderid){
    $('#orderList').empty();   
    $('#orderList').append(`
        <div class="col-md-4">                            
            <p>訂單編號</p>                                                      
         </div>
         <div class="col-md-4">                            
            <p>訂單用戶</p>                                                      
         </div>
         <div class="col-md-4">                            
            <p>訂單時間</p>                                                      
         </div>
        
    `);
    $.ajax({
        url: "http://localhost:8080/api/orders/orderid/"+orderid,
        type: "GET",
        dataType: "json",
        success: function (order) {           
           
                $('#orderList').append(`
                            <div class="col-md-4">                            
                                <p>${order.id}</p>                                                      
                             </div>
                             <div class="col-md-4">                            
                                <p>${order.username}</p>                                                      
                             </div>
                             <div class="col-md-4">                            
                                <p>${order.orderTime}</p>                                                      
                             </div>
                            
                        `);
           
        },
        error: function (xhr) {
            $('#loginMessage').text('帳號或密碼錯誤');
        }
    });
    showItemDetails(orderid);
}
function showItemDetails(orderid) {
    $('#itemList').empty();
    $('#itemList').append(`
        <div class="col-md-3">                            
            <p>產品編號</p>                                                      
         </div>
         <div class="col-md-3">                            
            <p>產品名稱</p>                                                      
         </div>
         <div class="col-md-3">                            
            <p>產品價格</p>                                                      
         </div>
        <div class="col-md-3">                            
          <p>數量</p>
         </div>
    `);
    $.ajax({
        url: "http://localhost:8080/api/items/"+orderid,
        type: "GET",
        dataType: "json",
        success: function (items) {           
            $.each(items, function (i, item) {
                $('#itemList').append(`
                            <div class="col-md-3">                            
                                <p>${item.pid}</p>                                                      
                             </div>
                             <div class="col-md-3">                            
                                <p>${item.productTitle}</p>                                                      
                             </div>
                             <div class="col-md-3">                            
                                <p>${item.productPrice}</p>                                                      
                             </div>
                            <div class="col-md-3">                            
                                <p>${item.quantity}</p>                                                      
                             </div>
                        `);
            });
        },
        error: function (xhr) {
            $('#loginMessage').text('帳號或密碼錯誤');
        }
    });

}
function addToCart(productId,qty) {
    const product = myproducts.find(p => p.id === productId);    
    console.log("qty:"+$(qty).val())
    const product2={...product,"quantity":$(qty).val()}    
    cart.push(product2);
    console.log("product:"+JSON.stringify(product2));
    alert(`已將 ${product2.title} 加入購物車`);
}
$(document).ready(start);

