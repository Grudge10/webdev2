# Week 4 Lab

## Task 1 - Product List Template

products.html renders the model list with th:each on the <li>; names via th:text="${p.name}". Empty-state message gated by th:if="${products.isEmpty()}" - shows only when the list is empty.
Evidence: screenshot/task1.png

## Task 2 - Product Detail Template

product-detail.html shows ${product.name}, ${product.price}, and nested ${product.category.name}.
Evidence: screenshot/task2.png

## Task 3 - Shared Navigation Fragment

fragments/navbar.html defines th:fragment="navbar". pulled in with th:replace="fragments/navbar :: navbar" on both pages (one shared source, no duplicated markup).
Evidence: screenshot/task3-1.png (list page), screenshot/task3-2.png (detail page)

## Task 4 - Create Form

product-form.html: <form th:action="@{/products}" th:object="${product}" method="post"> with th:field="*{name}" and th:field="*{price}". POST saves then returns "redirect:/products" (PRG).
Evidence: screenshot/task4-1.png (submission), screenshot/task4-2.png (redirected list with new product)

## Task 5 - Binding Errors

createProduct(@ModelAttribute Product product, BindingResult result) - BindingResult directly after the @ModelAttribute parameter; result.hasErrors() re-renders "product-form" without saving. th:errors="*{price}" shows the field-specific message (tested with "FREE" in the Double price field).
Evidence: screenshot/task5.png

## Handout "Try it yourself" answers

1. th:text="${student.enrollmentDate}"
2. One item = exactly one repeated <li>. zero items = zero repetitions (empty <ul>, no error, no placeholder).
3. th:replace="fragments/footer :: siteFooter"
4. <form th:action="@{/students}" th:object="${student}" method="post">
     <input type="text" th:field="*{name}" />
     <input type="text" th:field="*{email}" />
     <button type="submit">Save</button>
   </form>
5. public String createStudent(@ModelAttribute Student student, BindingResult result)
   - BindingResult must sit immediately after its @ModelAttribute parameter.
