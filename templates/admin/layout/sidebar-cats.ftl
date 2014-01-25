<#if category_title??>
    <h5>${category_title}</h5>
    <#assign i = 0>
    <#list categories as category>
        <a href="${category_urls[i]}">${category}</a><br/>
        <#assign i = i + 1>
    </#list>
</#if>
