const token = localStorage.getItem("accessToken");
if (!token) { alert("You must login"); window.location.href = "/view/login"; }

let currentPage = 0;
let totalPages = 0;

/* LOAD PRODUCTS */
function loadProducts(page = 0) {
  fetch(`/product/show-product?pageNo=${page}&pageSize=12`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const tbody = document.getElementById("productTableBody");
    tbody.innerHTML = "";

    const products = data.data.items;
    totalPages = data.data.totalPage;
    currentPage = data.data.pageNo;

    products.forEach(p => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${p.id}</td>
        <td>${p.productName}</td>
        <td>${p.group}</td>
        <td>${p.brand}</td>
        <td>${p.price}</td>
        <td><img src="/${p.imageUrl}" class="image-thumb"></td>
        <td class="actions">
          <button onclick="editProduct(${p.id})">Edit</button>
          <button onclick="deleteProduct(${p.id})">Delete</button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    renderPagination();
  });
}

/* PAGINATION */
function renderPagination() {
  const pageNumbers = document.getElementById("pageNumbers");
  pageNumbers.innerHTML = "";
  for (let i = 0; i < totalPages; i++) {
    const btn = document.createElement("button");
    btn.textContent = i + 1;
    if (i === currentPage) btn.disabled = true;
    btn.onclick = () => changePage(i);
    pageNumbers.appendChild(btn);
  }
}

function changePage(page) {
  if (page < 0 || page >= totalPages) return;
  loadProducts(page);
}

/* OVERLAY CLOSE ALL */
function closeAllPopup() {
  closeAddProduct();
  closeEditProduct();
}

/* ADD PRODUCT POPUP */
function openAddProduct() {
  document.getElementById("overlay").style.display = "block";
  document.getElementById("addProductForm").style.display = "block";
}

function closeAddProduct() {
  document.getElementById("overlay").style.display = "none";
  document.getElementById("addProductForm").style.display = "none";
  document.getElementById("productForm").reset();
  document.getElementById("groupOther").style.display = "none";
  document.getElementById("brandOther").style.display = "none";
}

/* SHOW OTHER INPUT */
function checkOther(type) {
  if (type === "group") {
    const sel = document.getElementById("groupSelect");
    document.getElementById("groupOther").style.display = sel.value === "other" ? "block" : "none";
  }
  if (type === "brand") {
    const sel = document.getElementById("brandSelect");
    document.getElementById("brandOther").style.display = sel.value === "other" ? "block" : "none";
  }
}

/* ADD PRODUCT SUBMIT */
document.getElementById("productForm").addEventListener("submit", function(e) {
  e.preventDefault();

  const formData = new FormData();
  formData.append("productName", this.productName.value);

  let group = this.groupSelect.value === "other" ? this.groupOther.value : this.groupSelect.value;
  let brand = this.brandSelect.value === "other" ? this.brandOther.value : this.brandSelect.value;

  formData.append("group", group.toLowerCase());
  formData.append("brand", brand.toLowerCase());
  formData.append("price", this.price.value);
  formData.append("imageFile", this.imageFile.files[0]);

  fetch("/product/add", {
    method: "POST",
    headers: { "Authorization": `Bearer ${token}` },
    body: formData
  })
  .then(res => res.json())
  .then(data => {
    if (data.status === 200) {
      alert("Product added!");
      closeAddProduct();
      loadProducts(currentPage);
    } else {
      alert("Failed: " + data.message);
    }
  });
});

/* EDIT PRODUCT */
function editProduct(id) {
  fetch(`/product/get/${id}`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    if (!data.data) return alert("Product not found");

    const p = data.data;

    document.getElementById("overlay").style.display = "block";
    document.getElementById("editProductForm").style.display = "block";

    document.getElementById("editId").value = p.id;
    document.getElementById("editProductName").value = p.productName;
    document.getElementById("editGroup").value = p.group;
    document.getElementById("editBrand").value = p.brand;
    document.getElementById("editPrice").value = p.price;
  });
}

/* SAVE EDIT */
function saveEdit() {
  const id = document.getElementById("editId").value;

  const dto = {
    productName: document.getElementById("editProductName").value,
    group: document.getElementById("editGroup").value,
    brand: document.getElementById("editBrand").value,
    price: document.getElementById("editPrice").value
  };

  const formData = new FormData();
  formData.append("product", new Blob([JSON.stringify(dto)], { type: "application/json" }));

  const newImage = document.getElementById("editImageFile").files[0];
  if (newImage) {
    formData.append("image", newImage);
  }

  fetch(`/product/update/${id}`, {
    method: "PUT",
    headers: { "Authorization": `Bearer ${token}` },
    body: formData
  })
  .then(res => res.json())
  .then(result => {
    if (result.status === 200) {
      alert("Product updated!");
      closeEditProduct();
      loadProducts(currentPage);
    } else {
      alert("Update failed: " + result.message);
    }
  });
}

/* CLOSE EDIT POPUP */
function closeEditProduct() {
  document.getElementById("editProductForm").style.display = "none";
  document.getElementById("overlay").style.display = "none";
}

/* DELETE PRODUCT */
function deleteProduct(id) {
  if (!confirm("Delete this product?")) return;

  fetch(`/product/delete/${id}`, {
    method: "DELETE",
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    if (data.status === 200) {
      alert("Deleted!");
      loadProducts(currentPage);
    } else {
      alert("Delete failed: " + data.message);
    }
  });
}

/* INIT */
loadProducts();
