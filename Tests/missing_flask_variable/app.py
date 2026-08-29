from flask import Flask, render_template

app = Flask(__name__)



# TEST 1

@app.route('/profile')
def profile():
    return render_template(
        'profile.html',
        username="Ali",
        age=25
    )


# TEST 2
@app.route('/dashboard')
def dashboard():
    return render_template(
        'dashboard.html',
        total_sales=5000,
        region="North"
    )


# TEST 3
@app.route('/settings')
def settings():
    return render_template('settings.html')


# TEST 4
@app.route('/about')
def about():
    return render_template(
        'about.html',
        company_name="TechCorp"
    )


# TEST 5
@app.route('/cart')
def cart():
    return render_template(
        'cart.html',
        item_name="Laptop",
        item_price=1500,
        quantity=1
    )

# TEST 6
@app.route('/notifications')
def notifications():
    return render_template(
        'notifications.html',
        unread_count=3,
        sender_name="Sara",
        latest_message="Are you free tomorrow?"
    )


# TEST 7
@app.route('/static-page')
def static_page():
    return render_template('static_page.html')


if __name__ == '__main__':
    app.run(debug=True)