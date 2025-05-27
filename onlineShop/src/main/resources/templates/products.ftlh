<#import "blocks/template.ftlh" as t>
<@t.template user>
    <h1 style="text-align: center">Search product</h1><hr>
    <#if user.email??>
        <div class="text-right mb-3">
            <a href="/cart" class="btn btn-dark">
                <i class="fas fa-shopping-cart"></i> Cart
            </a>
        </div>
    </#if>
    <form method="get" action="/" class="search-form">
        <div class="form-row align-items-center">
            <div class="col-md-10">
                <div class="input-group">
                    <input type="text" name="searchWord" class="form-control form-control-lg"
                           placeholder="Search for products..."
                           <#if searchWord??>value="${searchWord}"</#if>>
                </div>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-dark btn-lg btn-block">
                    <i class="fas fa-search"></i> Search
                </button>
            </div>
        </div>
    </form>
    <hr>
    <div class="card-columns">
        <#list products as product>
            <a href="/product/${product.id}" class="product-link">
                <div class="card text-white bg-dark my-3 product">

                    <img src="/images/${product.previewImageId}"
                         class="card-img-top">
                    <div class="card-body">
                        <h5 class="card-title">${product.title}</h5>
                        <p class="card-text">${product.price} $</p>
                    </div>
                </div>
            </a>
        <#else>
            <h1 style="text-align: center">Not found</h1>
        </#list>
    </div>
</@t.template>