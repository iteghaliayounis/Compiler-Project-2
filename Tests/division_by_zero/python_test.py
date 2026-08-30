from flask import Flask, render_template

app = Flask(__name__)


# TEST 1 — قسمة مباشرة على صفر (literal)
# متوقع: ZeroDivisionError: division by zero


@app.route("/direct-div")
def direct_div():
    x = 10 / 0
    return str(x)



# TEST 2 — modulo مباشر على صفر (literal)
# متوقع: ZeroDivisionError: integer division or modulo by zero


@app.route("/direct-mod")
def direct_mod():
    x = 10 % 0
    return str(x)



# TEST 3 — Constant Propagation: متغير قيمته صفر ثم قسمة عليه
# متوقع: ZeroDivisionError: division by zero


@app.route("/const-div")
def const_div():
    y = 0
    x = 10 / y
    return str(x)



# TEST 4 — Constant Propagation مع modulo
# متوقع: ZeroDivisionError: integer division or modulo by zero


@app.route("/const-mod")
def const_mod():
    n = 0
    x = 10 % n
    return str(x)



# TEST 5 — يجب ألا يبلّغ: القاسم قيمته غير صفرية معروفة


@app.route("/safe-const")
def safe_const():
    y = 5
    x = 10 / y
    return str(x)



# TEST 6 — يجب ألا يبلّغ: القاسم متغير مجهول القيمة وقت الـ compile (UNKNOWN)


@app.route("/unknown-divisor")
def unknown_divisor():
    x = 10 / count
    return str(x)



# TEST 7 — يجب ألا يبلّغ: إعادة تعيين المتغير قبل الاستخدام (constant propagation يحدّث القيمة)


@app.route("/reassigned")
def reassigned():
    y = 0
    y = 5
    x = 10 / y
    return str(x)



# TEST 8 — قسمة على صفر داخل if
# متوقع: ZeroDivisionError: division by zero


@app.route("/inside-if")
def inside_if():
    flag = True
    if flag:
        x = 10 / 0
        return str(x)
    return "OK"



# TEST 9 — قسمة على صفر داخل for
# متوقع: ZeroDivisionError: division by zero


@app.route("/inside-for")
def inside_for():
    items = [1, 2, 3]
    for i in items:
        x = 10 / 0
    return "OK"



# TEST 10 — قسمة على صفر داخل try/except
# متوقع: ZeroDivisionError: division by zero


@app.route("/inside-try")
def inside_try():
    try:
        x = 10 / 0
    except Exception as e:
        x = None
    return str(x)



# TEST 11 — سلسلة قسمة متتالية تنتهي بصفر (a / b / c)
# متوقع: ZeroDivisionError: division by zero


@app.route("/chained")
def chained():
    x = 100 / 5 / 0
    return str(x)



# TEST 12 — يجب ألا يبلّغ: متغير بنفس الاسم بدالة أخرى قيمته صفر، ما لازم يسرّب لهاد الدالة
# (كل دالة عندها knownConstants مستقلة عن الدوال التانية)


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
