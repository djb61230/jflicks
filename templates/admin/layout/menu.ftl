<div class="large-12 columns">
    <div class="nav-bar right">
        <ul class="button-group">
            <#assign i = 0>
            <#list menus as menu>
                <li><a href="${menu_urls[i]}" class="button">${menu}</a></li>
                <#assign i = i + 1>
            </#list>
        </ul>
    </div>
    <h1>jflicks media system<small> just the way you want it.</small></h1>
    <hr />
</div>
