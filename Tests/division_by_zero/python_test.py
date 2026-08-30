from flask import Flask, render_template

app = Flask(__name__)


# TEST 1 — قسمة مباشرة على صفر (literal)


@app.route("/direct-div")
def direct_div():
    x = 10 / 0
    return str(x)



# TEST 2 — modulo مباشر على صفر (literal)


@app.route("/direct-mod")
def direct_mod():
    x = 10 % 0
    return str(x)



# TEST 3 — Constant Propagation: متغير قيمته صفر ثم قسمة عليه


@app.route("/const-div")
def const_div():
    y = 0
    x = 10 / y
    return str(x)



# TEST 4 — Constant Propagation مع modulo


@app.route("/const-mod")
def const_mod():
    n = 0
    x = 10 % n
    return str(x)



# TEST 5 —  القاسم قيمته غير صفرية معروفة


@app.route("/safe-const")
def safe_const():
    y = 5
    x = 10 / y
    return str(x)



# TEST 6 —  القاسم متغير مجهول القيمةـ 


@app.route("/unknown-divisor")
def unknown_divisor():
    x = 10 / count
    return str(x)



# TEST 7 


@app.route("/reassigned")
def reassigned():
    y = 0
    y = 5
    x = 10 / y
    return str(x)



# TEST 8 — قسمة على صفر داخل if


@app.route("/inside-if")
def inside_if():
    flag = True
    if flag:
        x = 10 / 0
        return str(x)
    return "OK"



# TEST 9 — قسمة على صفر داخل for


@app.route("/inside-for")
def inside_for():
    items = [1, 2, 3]
    for i in items:
        x = 10 / 0
    return "OK"



# TEST 10 — قسمة على صفر داخل try/except


@app.route("/inside-try")
def inside_try():
    try:
        x = 10 / 0
    except Exception as e:
        x = None
    return str(x)



# TEST 11 — سلسلة قسمة متتالية تنتهي بصفر (a / b / c)


@app.route("/chained")
def chained():
    x = 100 / 5 / 0
    return str(x)



# TEST 12 
@app.route("/leak-a")
def leak_a():
    z = 0
    return "unused here"


@app.route("/leak-b")
def leak_b():
    z = 5
    x = 10 / z
    return str(x)


if __name__ == "__main__":
    app.run()
