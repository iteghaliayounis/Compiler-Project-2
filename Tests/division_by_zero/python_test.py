from flask import Flask, render_template

app = Flask(__name__)


@app.route("/direct-div")
def direct_div():
    x = 10 / 0
    return str(x)




@app.route("/direct-mod")
def direct_mod():
    x = 10 % 0
    return str(x)



@app.route("/const-div")
def const_div():
    y = 0
    x = 10 / y
    return str(x)




@app.route("/const-mod")
def const_mod():
    n = 0
    x = 10 % n
    return str(x)





@app.route("/safe-const")
def safe_const():
    y = 5
    x = 10 / y
    return str(x)





@app.route("/unknown-divisor")
def unknown_divisor():
    x = 10 / count
    return str(x)





@app.route("/reassigned")
def reassigned():
    y = 0
    y = 5
    x = 10 / y
    return str(x)




@app.route("/inside-if")
def inside_if():
    flag = True
    if flag:
        x = 10 / 0
        return str(x)
    return "OK"




@app.route("/inside-for")
def inside_for():
    items = [1, 2, 3]
    for i in items:
        x = 10 / 0
    return "OK"




@app.route("/inside-try")
def inside_try():
    try:
        x = 10 / 0
    except Exception as e:
        x = None
    return str(x)





@app.route("/chained")
def chained():
    x = 100 / 5 / 0
    return str(x)



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
