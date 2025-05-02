<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>webShop</h1><hr>
<h3>Information about user ${user.name}</h3>
<b>Email: </b> ${user.email}<br>
<h4>User products ${user.name}</h4>
<#list products as product>
    <div>
        <p><b>${product.title}</b> ${product.price} USD | <a href="/product/${product.id}">Details...</a></p>
    </div>
    <#else>
    <h3>No products yet</h3>
</#list>
<#if errorMessage??>
    <h2 style="color: red">${errorMessage}</h2>
</#if>
</body>
</html>