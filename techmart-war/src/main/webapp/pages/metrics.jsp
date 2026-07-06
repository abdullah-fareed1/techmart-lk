<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>System Metrics | TechMart</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>body { font-family: 'Inter', sans-serif; }</style>
</head>
<body class="bg-gray-900 text-gray-100 min-h-screen p-6">

<div class="max-w-5xl mx-auto">

    <header class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">📊 TechMart System Metrics</h1>
        <a href="pos" class="text-sm font-semibold text-indigo-400 hover:text-indigo-300">← Back to POS</a>
    </header>

    <jsp:include page="metricsBar.jsp" />

    <div class="bg-gray-800 border border-gray-700 rounded-xl p-5">
        <h2 class="text-lg font-semibold mb-2">What these numbers mean</h2>
        <ul class="text-sm text-gray-400 space-y-1 list-disc list-inside">
            <li><span class="text-gray-200 font-medium">Total Orders</span> — orders successfully placed via checkout.</li>
            <li><span class="text-gray-200 font-medium">JMS Messages Processed</span> — orders confirmed by the OrderProcessingMDB after async stock deduction.</li>
            <li><span class="text-gray-200 font-medium">Avg Processing Time</span> — mean time the MDB takes to process one queued order.</li>
            <li><span class="text-gray-200 font-medium">Catalogue Load Time</span> — cumulative time spent fetching products from the database.</li>
        </ul>
    </div>

</div>

</body>
</html>