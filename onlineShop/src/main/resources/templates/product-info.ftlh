<#import "blocks/template.ftlh" as t>
<@t.template user>
    <div id="myCarousel" class="carousel slide" data-ride="carousel">
        <ol class="carousel-indicators">
            <#list images as image>
                <li data-target="#myCarousel" data-slide-to="0" class=""></li>
            </#list>
        </ol>
        <div class="carousel-inner">
            <#list images as image>
                <div class="carousel-item <#if image.previewImage>active</#if>">
                    <svg class="bd-placeholder-img" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg"
                         role="img" aria-label=" :  " preserveAspectRatio="xMidYMid slice" focusable="false">
                        <title></title>
                        <rect width="100%" height="100%" fill="#777"></rect>
                        <text x="50%" y="50%" fill="#777" dy=".3em"></text>
                    </svg>

                    <div class="container">
                        <div class="carousel-caption">
                            <a href="/images/${image.id}">
                                <img class="product" style="height: 100px" src="/images/${image.id}">
                            </a>
                        </div>
                    </div>
                </div>
            </#list>

        </div>
        <a class="carousel-control-prev" href="#myCarousel" role="button" data-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="sr-only">Previous</span>
        </a>
        <a class="carousel-control-next" href="#myCarousel" role="button" data-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="sr-only">Next</span>
        </a>
    </div>
    <hr>
    <p><h1 style="text-align: center">${product.title} | ${product.price} $</h1></p>
    <p style="text-align: center">${product.description}</p>
    <hr>

    <!-- Форма для добавления в корзину -->
    <#if user?? && user.id??>
        <div style="text-align: center; margin-top: 20px;">
            <form action="/cart/add/${product.id}" method="post">
                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                <label for="quantity">Количество:</label>
                <input type="number" id="quantity" name="quantity" value="1" min="1" required style="width: 60px;"/>
                <button type="submit" class="btn btn-primary">Добавить в корзину</button>
            </form>
        </div>
    <#else>
        <p style="text-align: center; color: red;">Войдите, чтобы добавить товар в корзину.</p>
    </#if>

    <hr>
    <p style="text-align: center">
        <a href="/user/${authorProduct.id}">
            <img src="/static/images/avatar.png" alt="mdo" width="32" height="32" class="rounded-circle"> ${authorProduct.name}
        </a>
    </p>

</@t.template>