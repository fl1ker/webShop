<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>webShop</title>
</head>
<body>
<h1>webShop</h1><hr>
<h4>Admin panel</h4>
<table border="1">
    <tr>
        <th>Email</th>
        <th>Active</th>
        <th>Role</th>
        <th>Ban</th>
        <th>Edit</th>
        <th>Information</th>
    </tr>

    <#list users as user>
        <tr>
            <th>${user.email}</th>
            <th><#if user.active>true<#else>false</#if></th>
            <th><#list user.roles as role>${role}, </#list></th>
            <th>
                <form action="/admin/user/ban/${user.id}" method="post">
                    <input type="hidden" name="_csrf" value="${_csrf.token}">
                    <input type="submit" value="BAN">
                </form> </th>
            <th><a href="/admin/user/edit/${user.id}">Edit</a></th>
            <th><a href="/user/${user.id}">Information</a></th>
        </tr>
        <#else>
        <h3>No users</h3>
    </#list>

</table>

</body>
</html>