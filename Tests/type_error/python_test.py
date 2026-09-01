from flask import Flask, render_template

app = Flask(__name__)


@app.route("/")
def home():

    x1 = 10
    for i in x1:
        print(i)


    x2 = 10
    print(len(x2))


    x3 = 5
    print(x3[0])

    x4 = "Sara" + 4



    x5 = "Sara" - 4



    x6 = "Sara" - "Ali"


    x7 = "Sara" < 4


    x8 = None
    x8_result = x8 + 5



    # تمرير متغيرات للقالب ليتم فحص الفلاتر
    age = 22           # int
    name = "Sara"      # str
    price = 99.5       # float
    items = [1, 2, 3]  # list


    return render_template(
            "htmlTestErrors.html",
            age=age,
            name=name,
            price=price,
            items=items
        )
