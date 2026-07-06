<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>POS | TechMart</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-gray-900 text-gray-100 min-h-screen p-6">

<div class="max-w-7xl mx-auto">

    <header class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">🛒 TechMart POS Terminal</h1>
        <a href="transactions" class="text-sm font-semibold text-indigo-400 hover:text-indigo-300">View Transactions →</a>
    </header>

    <jsp:include page="metricsBar.jsp" />

    <c:if test="${param.error == 'nocustomer'}">
        <div class="mb-4 rounded-lg bg-amber-900/40 border border-amber-700 text-amber-300 px-4 py-3 text-sm">
            Please select a customer before completing the sale.
        </div>
    </c:if>
    <c:if test="${param.error == 'emptycart'}">
        <div class="mb-4 rounded-lg bg-amber-900/40 border border-amber-700 text-amber-300 px-4 py-3 text-sm">
            Your cart is empty — add a product before completing the sale.
        </div>
    </c:if>
    <c:if test="${param.error == 'stock'}">
        <div class="mb-4 rounded-lg bg-red-900/40 border border-red-700 text-red-300 px-4 py-3 text-sm">
            Not enough stock for "${param.item}" — only ${param.available} available. Your cart has been restored.
        </div>
    </c:if>

    <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">

        <div class="lg:col-span-3 bg-gray-800 border border-gray-700 rounded-xl p-5">
            <h2 class="text-lg font-semibold mb-4">Products</h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <c:forEach var="p" items="${products}">
                    <div class="bg-gray-900 border border-gray-700 rounded-lg p-4 flex flex-col justify-between">
                        <div>
                            <div class="font-medium">${p.name}</div>
                            <div class="text-sm text-gray-400 mt-1">$${p.price} &middot; Stock: ${p.stock}</div>
                        </div>
                        <form action="addToCart" method="post" class="flex items-center gap-2 mt-3">
                            <input type="hidden" name="productId" value="${p.id}">
                            <input type="number" name="qty" value="1" min="1"
                                   class="w-16 bg-gray-800 border border-gray-600 rounded-md px-2 py-1 text-sm text-gray-100 focus:outline-none focus:border-indigo-500">
                            <button type="submit"
                                    class="bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-medium px-3 py-1.5 rounded-md">
                                Add
                            </button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </div>

        <div class="lg:col-span-2 bg-gray-800 border border-gray-700 rounded-xl p-5 flex flex-col">
            <h2 class="text-lg font-semibold mb-4">Current Sale</h2>

            <form action="placeOrder" method="post" class="flex flex-col flex-1">

                <label class="text-xs uppercase tracking-wide text-gray-400 mb-1">Customer</label>
                <select name="customerId" required
                        class="mb-4 bg-gray-900 border border-gray-600 rounded-md px-3 py-2 text-sm text-gray-100 focus:outline-none focus:border-indigo-500">
                    <option value="" disabled selected>Select a customer</option>
                    <c:forEach var="cust" items="${customers}">
                        <option value="${cust.id}">${cust.name}</option>
                    </c:forEach>
                </select>

                <div class="flex-1 border border-gray-700 rounded-lg overflow-hidden">
                    <table class="w-full text-sm">
                        <thead>
                        <tr class="bg-gray-900 text-gray-400 uppercase text-xs">
                            <th class="text-left px-3 py-2">Product</th>
                            <th class="text-center px-3 py-2">Qty</th>
                            <th class="text-right px-3 py-2">Subtotal</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty cart}">
                            <tr>
                                <td colspan="3" class="text-center italic text-gray-500 px-3 py-6">Cart is empty</td>
                            </tr>
                        </c:if>
                        <c:set var="grandTotal" value="0"/>
                        <c:forEach var="entry" items="${cart}">
                            <c:set var="lineTotal" value="${entry.key.price * entry.value}"/>
                            <c:set var="grandTotal" value="${grandTotal + lineTotal}"/>
                            <tr class="border-t border-gray-700">
                                <td class="px-3 py-2">${entry.key.name}</td>
                                <td class="text-center px-3 py-2">${entry.value}</td>
                                <td class="text-right px-3 py-2">$<fmt:formatNumber value="${lineTotal}" maxFractionDigits="2"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="flex justify-between items-center mt-4 mb-4">
                    <span class="text-gray-400 text-sm">Total</span>
                    <span class="text-xl font-bold text-emerald-400">$<fmt:formatNumber value="${grandTotal}" maxFractionDigits="2"/></span>
                </div>

                <button type="submit"
                        class="bg-emerald-600 hover:bg-emerald-500 text-white font-semibold py-3 rounded-lg">
                    Complete Sale
                </button>
            </form>
        </div>

    </div>
</div>

</body>
</html>
