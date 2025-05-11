<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">My account</h1>

    <hr>
    <br>
    <div style="text-align: center">
        <img src="/static/images/avatar.png"
             style="border:2px solid black; border-radius: 20px" height="120">
        <br>
        <br>
        <h2>${user.name}</h2>
        <br>
        Email: <b>${user.email}</b>
        <br>
        <#if user.isAdmin()>
            <a class="btn btn-dark mb-3" style="text-align: center; width: 100%"
               href="/admin">
                Admin panel
            </a><br>
        </#if>
        <a class="btn btn-dark mb-3" style="text-align: center; width: 100%"
           href="/my/products">
            My products
        </a>
        <form action="/logout" method="post">
            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
            <button type="submit" style="text-align: center; width: 100%" class="btn btn-danger mb-2">
                Logout
            </button>
        </form>
    </div>

    <hr>
    <h2 style="text-align: center">История покупок</h2>
    <div style="margin: 20px auto; width: 90%">
        <#if orders?? && orders?size gt 0>
            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Продукт</th>
                    <th>Количество</th>
                    <th>Дата покупки</th>
                </tr>
                </thead>
                <tbody>
                <#list orders as order>
                    <tr>
                        <td>${order.product.title}</td>
                        <td>${order.quantity}</td>
                        <td>${order.formattedDate}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        <#else>
            <p style="text-align: center">У вас пока нет покупок.</p>
        </#if>
    </div>
</@t.template>
