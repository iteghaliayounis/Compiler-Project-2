from flask import Flask, render_template

app = Flask(__name__)


def helper():
    helper_value = 10
    return helper_value


@app.route("/use-after-helper")
def use_after_helper():
    print(helper_value)
    return "OK"


def helper_two():
    inner_total = 99
    return inner_total


@app.route("/use-inside-if")
def use_inside_if():
    condition = True
    if condition:
        print(inner_total)
    return "OK"


page_title = "My Store"


@app.route("/use-global")
def use_global():
    print(page_title)
    return "OK"


@app.route("/loop-var-after")
def loop_var_after():
    total = 0
    for number in [1, 2, 3]:
        total = total + number
    print(number)
    return str(total)





@app.route("/same-function")
def same_function():
    quantity = 5
    print(quantity)
    return str(quantity)


@app.route("/shared-a")
def shared_a():
    shared_name = "A"
    return shared_name


@app.route("/shared-b")
def shared_b():
    print(shared_name)
    return "OK"



@app.route("/exception-var")
def exception_var():
    try:
        result = 10 / 2
    except Exception as e:
        result = None
    print(e)
    return str(result)





@app.route("/never-defined")
def never_defined():
    print(completely_unknown_name)
    return "OK"





def compute_discount():
    discount_rate = 0.1
    return discount_rate


@app.route("/second-case")
def second_case():
    print(discount_rate)
    return "OK"




@app.route("/classic-unbound")
def classic_unbound():
    print(status)
    status = "active"
    return status


if __name__ == "__main__":
    app.run()
