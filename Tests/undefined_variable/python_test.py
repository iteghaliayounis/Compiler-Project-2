from flask import Flask, render_template

app = Flask(__name__)


# TEST 1


@app.route("/direct")
def direct_usage():
    print(username)

    return "OK"



# TEST 2


@app.route("/arithmetic")
def arithmetic_usage():
    total = price + tax

    return str(total)



# TEST 3


@app.route("/condition")
def condition_usage():
    if is_admin:
        return "Admin"

    return "User"



# TEST 4


@app.route("/loop")
def loop_usage():
    for item in items:
        print(item)

    return "OK"



# TEST 5


@app.route("/argument")
def argument_usage():
    result = len(data)
    return str(result)



# TEST 6


@app.route("/list")
def list_usage():
    values = [first_value, second_value, third_value]

    return str(values)



# TEST 7

@app.route("/dictionary")
def dictionary_usage():
    user = {
        "name": user_name,
        "age": user_age
    }

    return str(user)


# TEST 8

@app.route("/return")
def return_usage():
    message = "Hello " + display_name

    return message


# TEST 9

@app.route("/multiple")
def multiple_usage():
    result = username + city + country

    return result

# TEST 10
@app.route("/multiple")
def multiple_usage():
    name = "Ali"
    return render_template("index.html", name=name, age=age)

# TEST 11
@app.route("/comprehension")
def comprehension_usage():
    names = list(users)
    return render_template("index.html", names=names)

# TEST 12
@app.route("/")
def home():
    try:
        result = process_data(data)
    except Exception as e:
        result = None
    return render_template("index.html", result=result)

if __name__ == "__main__":
    app.run()

