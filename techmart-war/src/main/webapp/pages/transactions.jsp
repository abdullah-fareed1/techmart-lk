<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>Transactions | TechMart</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-gray-900 text-gray-100 min-h-screen p-6">

<div class="max-w-6xl mx-auto">

    <header class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">📋 Recent Transactions</h1>
        <a href="pos" class="text-sm font-semibold text-indigo-400 hover:text-indigo-300">← Back to POS</a>
    </header>

    <jsp:include page="metricsBar.jsp" />

    <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">


        <div class="bg-gray-800 border border-gray-700 rounded-xl p-5">
            <h2 class="text-lg font-semibold mb-4">Recent Orders</h2>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                    <tr class="bg-gray-900 text-gray-400 uppercase text-xs">
                        <th class="text-left px-3 py-2">ID</th>
                        <th class="text-left px-3 py-2">Customer</th>
                        <th class="text-right px-3 py-2">Total</th>
                        <th class="text-center px-3 py-2">Status</th>
                        <th class="text-left px-3 py-2">Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${empty orders}">
                        <tr><td colspan="5" class="text-center italic text-gray-500 px-3 py-6">No orders yet</td></tr>
                    </c:if>
                    <c:forEach var="o" items="${orders}">
                        <tr class="border-t border-gray-700">
                            <td class="px-3 py-2">#${o.id}</td>
                            <td class="px-3 py-2">${o.customer.name}</td>
                            <td class="text-right px-3 py-2">$<fmt:formatNumber value="${o.totalAmount}" maxFractionDigits="2"/></td>
                            <td class="text-center px-3 py-2">
                                <c:choose>
                                    <c:when test="${o.status == 'CONFIRMED'}">
                                        <span class="bg-emerald-900/50 text-emerald-400 border border-emerald-700 px-2 py-0.5 rounded-full text-xs font-medium">CONFIRMED</span>
                                    </c:when>
                                    <c:when test="${o.status == 'SHIPPED'}">
                                        <span class="bg-blue-900/50 text-blue-400 border border-blue-700 px-2 py-0.5 rounded-full text-xs font-medium">SHIPPED</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="bg-amber-900/50 text-amber-400 border border-amber-700 px-2 py-0.5 rounded-full text-xs font-medium">PENDING</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="px-3 py-2 text-gray-400">${o.orderDate}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>


        <div class="bg-gray-800 border border-gray-700 rounded-xl p-5">
            <h2 class="text-lg font-semibold mb-4">Recent Notifications</h2>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                    <tr class="bg-gray-900 text-gray-400 uppercase text-xs">
                        <th class="text-left px-3 py-2">Order</th>
                        <th class="text-left px-3 py-2">Message</th>
                        <th class="text-center px-3 py-2">Status</th>
                        <th class="text-left px-3 py-2">Sent At</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${empty notifications}">
                        <tr><td colspan="4" class="text-center italic text-gray-500 px-3 py-6">No notifications yet</td></tr>
                    </c:if>
                    <c:forEach var="n" items="${notifications}">
                        <tr class="border-t border-gray-700">
                            <td class="px-3 py-2">#${n.order.id}</td>
                            <td class="px-3 py-2 text-gray-300">${n.message}</td>
                            <td class="text-center px-3 py-2">
                                <span class="bg-emerald-900/50 text-emerald-400 border border-emerald-700 px-2 py-0.5 rounded-full text-xs font-medium">${n.status}</span>
                            </td>
                            <td class="px-3 py-2 text-gray-400">${n.sentAt}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</div>

</body>
</html>
