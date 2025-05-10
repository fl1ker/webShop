<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">Ваша корзина</h1>
    <div class="container">
        <#if cart?? && cart.items?size gt 0>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Товар</th>
                    <th>Цена</th>
                    <th>Количество</th>
                    <th>Итого</th>
                    <th>Действия</th>
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
                                <button type="submit" class="btn btn-danger btn-sm">Удалить</button>
                            </form>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
            <h3 style="text-align: right">Общая стоимость: ${totalPrice} $</h3>
        <#else>
            <p style="text-align: center">Ваша корзина пуста.</p>
        </#if>
        <div style="text-align: center; margin-top: 20px;">
            <a href="/" class="btn btn-secondary">Продолжить покупки</a>
        </div>
    </div>
</@t.template>