<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
    <div class="bg-gray-800 border border-gray-700 rounded-xl p-4">
        <div class="text-xs uppercase tracking-wide text-gray-400 mb-1">Total Orders</div>
        <div class="text-2xl font-bold text-emerald-400">${totalOrders}</div>
    </div>
    <div class="bg-gray-800 border border-gray-700 rounded-xl p-4">
        <div class="text-xs uppercase tracking-wide text-gray-400 mb-1">JMS Messages Processed</div>
        <div class="text-2xl font-bold text-indigo-400">${processedMessages}</div>
    </div>
    <div class="bg-gray-800 border border-gray-700 rounded-xl p-4">
        <div class="text-xs uppercase tracking-wide text-gray-400 mb-1">Avg Processing Time</div>
        <div class="text-2xl font-bold text-indigo-400">${avgOrderTime} <span class="text-sm font-medium text-gray-400">ms</span></div>
    </div>
    <div class="bg-gray-800 border border-gray-700 rounded-xl p-4">
        <div class="text-xs uppercase tracking-wide text-gray-400 mb-1">Catalog Load Time</div>
        <div class="text-2xl font-bold text-indigo-400">${totalProductFetchTime} <span class="text-sm font-medium text-gray-400">ms</span></div>
    </div>
</div>
