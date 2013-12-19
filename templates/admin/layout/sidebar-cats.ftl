<#if category_title??>
    <h5>${category_title}</h5>
    <ul class="side-nav">
    <#assign i = 0>
    <#list categories as category>
        <li><a href="${category_urls[i]}">${category}</a></li>
        <#assign i = i + 1>
    </#list>
    </ul>
</#if>
