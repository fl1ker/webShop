<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">shopping cart</h1>
    <div class="container">
        <#if cart?? && cart.items?size gt 0>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Product</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Summary</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <#list cart.items as item>
                    <tr>
                        <td><a href="/product/${item.product.id}">${item.product.title}</a></td>
                        <td>${item.product.price} $</td>
                        <td>${item.quantity}</td>
                        <td>${item.product.price * item.quantity} $</td>
                        <td>
                            <form action="/cart/remove/${item.id}" method="post">
                                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
            <h3 style="text-align: right">Total price: ${totalPrice} $</h3>
        <#else>
            <p style="text-align: center">Your shopping cart is empty</p>
        </#if>
        <div class="text-center mt-4">
            <form method="post" action="/cart/checkout" class="d-inline">
                <input type="hidden" name="_csrf" value="${_csrf.token}">
                <button type="submit" class="checkout-btn">
                    Complete Purchase
                </button>
            </form>
            <a href="/" class="btn btn-outline-secondary ml-3">
                Continue Shopping
            </a>
        </div>
    </div>
</@t.template>