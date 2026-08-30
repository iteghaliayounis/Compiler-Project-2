from flask import Flask, render_template

app = Flask(__name__)


@app.route("/")
def home():



    # Test 1: استدعاء method غير موجودة على str

    attr_x1 = "hello"
    attr_x1.push("!")

    # Test 2: استدعاء method نصية على int

    attr_y1 = 5
    attr_y1.upper()

    # Test 3: استدعاء method على None
    attr_z1 = None
    attr_z1.upper()


    # Test 4: استدعاء method خاطئة على list (الصح append مو add)
    attr_lst1 = [1, 2, 3]
    attr_lst1.add(4)



    ok_str1 = "hello"
    ok_str1.upper()

    ok_list1 = [1, 2, 3]
    ok_list1.append(4)

    ok_dict1 = {"a": 1}
    ok_dict1.get("a")


    ok_chain1 = "hello"
    ok_chain1.strip().upper()


    ok_unknown1 = int("10")
    ok_unknown1.bit_length()



    attr_cond = "hi"
    if attr_cond.foo():
        pass

    attr_arg = 5
    print(attr_arg.bar())


    def greet(name, msg):
        print(name, msg)


    # Test 12: attribute access على نتيجة استدعاء دالة (يجب ألا يظهر خطأ - UNKNOWN)
    unknown_result = greet("a", "b")
    unknown_result.whatever()

    # Test 13: attribute صحيحة داخل return statement جوا دالة
    def get_upper(s):
        return s.upper()  

    # Test 14: attribute access متسلسل بعد method ما بترجع str

    attr_chain2 = [1, 2, 3]
    attr_chain2.append(4).sort()



    name = "Sara"     
    items = [1, 2, 3]  


    return render_template("jinja_test.html", name=name, items=items)
