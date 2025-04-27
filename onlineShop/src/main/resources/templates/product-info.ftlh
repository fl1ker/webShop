<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>hello, its my OnlimeShop</h1><hr>
<h4>All details about product</h4>
<b>Product name:</b>${product.title}<br>
<b>Product description:</b>${product.description}<br>
<b>Product price:</b>${product.price} USD<br>
<b>Product author:</b>${product.author}<br>
<hr>
<h3>Create new product</h3>
<form action="/product/delete/${product.id}" method="post">
    <input type="submit" value="delete product"/>
</form>
</body>
</html>