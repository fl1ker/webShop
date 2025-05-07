<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">Edit Product</h1>
    <hr>
    <form action="/product/edit/${product.id}" method="post" enctype="multipart/form-data">
        <!-- Title -->
        <div class="form-group">
            <label for="title">Product name</label>
            <input type="text" required class="form-control" id="title"
                   name="title" value="${product.title}">
        </div>

        <!-- Price -->
        <div class="form-group">
            <label for="price">Product price</label>
            <input type="number" required class="form-control" id="price"
                   name="price" value="${product.price}">
        </div>

        <!-- Description -->
        <div class="form-group">
            <label for="description">Product description</label>
            <textarea required class="form-control" id="description"
                      name="description">${product.description}</textarea>
        </div>

        <!-- Image 1 -->
        <div class="form-group">
            <label for="file1">Preview image</label>
            <input type="file" class="form-control-file" name="file1" id="file1">
        </div>

        <!-- Image 2 -->
        <div class="form-group">
            <label for="file2">Additional image</label>
            <input type="file" class="form-control-file" name="file2" id="file2">
        </div>

        <!-- Image 3 -->
        <div class="form-group">
            <label for="file3">Additional image</label>
            <input type="file" class="form-control-file" name="file3" id="file3">
        </div>

        <input type="hidden" name="_csrf" value="${_csrf.token}"/>
        <button type="submit" class="btn btn-dark mt-3" style="width: 100%">Save changes</button>
    </form>

</@t.template>
