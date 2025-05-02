<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>webShop</title>
</head>
<body>
<h1>webShop</h1><hr>
<#if user.email??>
    <h3>User name: <i>${user.name}</i></h3>
    <form action="/logout" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" value="Logout"/>
    </form>
    <#if user.isAdmin()>
        <a href = "/admin">Admin panel</a>
    </#if>
<#else>
    <a href="/login">Log in</a><hr>
</#if>
<hr>
    <h4>here is all products</h4>
    <form action="/" method="get">
        Finding by product name: <input type="text" name="title"><br>
        <input type="submit" value="Find"/>
    </form>
    <#list products as product>
        <div>
            <p><b>${product.title}</b> ${product.price} USD | <a href="/product/${product.id}">Details...</a></p>
        </div>
        <#else>
        <h3>No Products</h3>
    </#list>
    <#if user.email??>
    <hr>
    <h3>Create new product</h3>
    <form action="/product/create" method="post" enctype="multipart/form-data">
        Post name: <input type="text" name="title"><br><br>
        Post description: <input type="text" name="description"><br><br>
        Post price: <input type="number" name="price"><br><br>
        Preview image: <input type="file" name="file1"><br><br>
        Second image: <input type="file" name="file2"><br><br>
        Third image: <input type="file" name="file3"><br><br>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
        <input type="submit" value="Add product"/>
    </form>
    </#if>
</body>
</html>