<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>hello, its my OnlimeShop</h1><hr>
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
    <hr>
    <h3>Create new product</h3>
    <form action="/product/create" method="post">
        Post name: <input type="text" name="title"><br><br>
        Post description: <input type="text" name="description"><br><br>
        Post price: <input type="number" name="price"><br><br>
        Post author: <input type="text" name="author"><br><br>
        <input type="submit" value="Add product"/>
    </form>
</body>
</html>