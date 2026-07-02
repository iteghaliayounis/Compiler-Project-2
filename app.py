from flask import Flask, render_template, request, redirect, url_for, abort

# تعريف مصفوفة المنتجات
products = [
    {
        "id": 1,
        "name": "Laptop",
        "price": 750,
        "image": "baby.jpg",
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
            "name": "Wool Jacket",
            "price": 100,
            "image": "Coat.jpg",
            "description": "navy wool jacket."
        },
         {
                    "id": 4,
                    "name": "Baby Overalls",
                    "price": 700,
                    "image": "overalls.jpg",
                    "description": "burnt brown baby overalls."
                }
    ]


app = Flask(__name__)

@app.route("/")
def products_page():
    return render_template("products.html", products=products)

@app.route("/product/<int:pid>")
def product_detail(pid):
    product = next((p for p in products if p.get("id") == pid), None)
    if product is None:
        abort(404)
    return render_template(
        "product_detail.html",
        product=product,
    )

@app.route("/add", methods=["GET", "POST"])
def add_product():
    if request.method == "POST":
        name = request.form.get("name", "").strip()
        price_raw = request.form.get("price", "").strip()
        image = request.form.get("image", "").strip()
        description = request.form.get("description", "").strip()

        try:
            price = int(price_raw)
        except Exception:
            try:
                price = int(float(price_raw))
            except Exception:
                price = 0

        new = {
            "id": (max((p.get("id", 0) for p in products), default=0) + 1),
            "name": name,
            "price": price,
            "image": image,
            "description": description
        }
        products.append(new)
        return redirect(url_for("products_page"))

    return render_template("add_product.html")

# 🚨 Route حذف المنتج
@app.route("/delete/<int:pid>", methods=["POST"])
def delete_product(pid):
    global products
    products = [p for p in products if p.get("id") != pid]
    return redirect(url_for("products_page"))

# تشغيل السيرفر
if __name__ == "__main__":
    app.run(debug=True)

