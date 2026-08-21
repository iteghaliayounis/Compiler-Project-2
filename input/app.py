from flask import Flask, render_template, request, redirect, url_for, abort

# تعريف مصفوفة المنتجات
products = [
    {
        "id": 1,
        "name": "Laptop",
        "price": 750,
        "image": "laptop.jpg",
        "description": "High performance laptop."
    },
    {
        "id": 2,
        "name": "Dress",
        "price": 20,
        "image": "dress.jpg",
        "description": "Dress Baby Red."
    },
    {
            "id": 3,
            "name": "phone",
            "price": 4590,
            "image": "phone.jpg",
            "description": "fpon Baby Red."
    }
]

app = Flask(__name__)

@app.route("/")
def products_page():
    return render_template("index.html", products=products)

@app.route("/product/<int:pid>")
def product_detail(pid):
    product = next((p for p in products if p.get("id") == pid), None)
    if product is None:
        abort(404)
    return render_template("product_details.html", product=product)

@app.route("/add", methods=["GET", "POST"])
def add_product():
    return render_template("add_product.html")

@app.route("/delete/<int:pid>", methods=["POST"])
def delete_product(pid):
    for p in products:
        if p.get("id") == pid:
            products.remove(p)
            return redirect(url_for("products_page"))
    return redirect(url_for("products_page"))

if __name__ == "__main__":
    app.run(debug=True)