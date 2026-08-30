from flask import Flask, render_template

app = Flask(__name__)


@app.route("/")
def home():
    # ==========================================================
    # اختبار معزول لخطأ Invalid Attribute Access (AttributeError)
    # Python Semantic Analysis
    # ==========================================================

    # ─────────────────────────────────────────────
    # 1. أخطاء AttributeError الأساسية (المتوقع ظهورها)
    # ─────────────────────────────────────────────

    # Test 1: استدعاء method غير موجودة على str
    # المتوقع: AttributeError: 'str' object has no attribute 'push'
    attr_x1 = "hello"
    attr_x1.push("!")

    # Test 2: استدعاء method نصية على int
    # المتوقع: AttributeError: 'int' object has no attribute 'upper'
    attr_y1 = 5
    attr_y1.upper()

    # Test 3: استدعاء method على None
    # المتوقع: AttributeError: 'NoneType' object has no attribute 'upper'
    attr_z1 = None
    attr_z1.upper()

    # Test 4: استدعاء method خاطئة على list (الصح append مو add)
    # المتوقع: AttributeError: 'list' object has no attribute 'add'
    attr_lst1 = [1, 2, 3]
    attr_lst1.add(4)

    # ─────────────────────────────────────────────
    # 2. حالات صحيحة (يجب ألا تطلع أي AttributeError)
    # ─────────────────────────────────────────────

    # Test 5: استدعاء method صحيحة على str
    ok_str1 = "hello"
    ok_str1.upper()

    # Test 6: استدعاء method صحيحة على list
    ok_list1 = [1, 2, 3]
    ok_list1.append(4)

    # Test 7: استدعاء method صحيحة على dict
    ok_dict1 = {"a": 1}
    ok_dict1.get("a")

    # Test 8: chaining صحيح (str → str → method نصية أخرى)
    ok_chain1 = "hello"
    ok_chain1.strip().upper()

    # Test 9: attribute access على نوع غير معروف وقت الـ compile (لازم ما يطلع خطأ)
    ok_unknown1 = int("10")
    ok_unknown1.bit_length()

    # ─────────────────────────────────────────────
    # 3. حالات حواف (Edge Cases) — للتأكد من التغطية الكاملة بكل السياقات
    # ─────────────────────────────────────────────

    # Test 10: attribute access جوا شرط if
    attr_cond = "hi"
    if attr_cond.foo():
        pass

    # Test 11: attribute access كـ argument لدالة تانية
    attr_arg = 5
    print(attr_arg.bar())

    # دالة مساعدة لاختبار Test 12
    def greet(name, msg):
        print(name, msg)

    # Test 12: attribute access على نتيجة استدعاء دالة (يجب ألا يطلع خطأ - UNKNOWN)
    unknown_result = greet("a", "b")
    unknown_result.whatever()

    # Test 13: attribute صحيحة داخل return statement جوا دالة
    def get_upper(s):
        return s.upper()   # ✅ لازم يعدي بدون خطأ لو s من نوع str معروف

    # Test 14: attribute access متسلسل بعد method ما بترجع str
    # (false negative متوقع ومقبول حسب فلسفة "False Positive أسوأ من False Negative")
    attr_chain2 = [1, 2, 3]
    attr_chain2.append(4).sort()

    # ─────────────────────────────────────────────
    # 4. تمرير متغيرات لقالب Jinja المعزول (لدعم اختبار AttributeError بالجينجا)
    # ─────────────────────────────────────────────
    name = "Sara"       # str  → لاختبار name.push / name.upper بالقالب
    items = [1, 2, 3]   # list → لاختبار items.foo / items.append بالقالب

    return render_template("jinja_test.html", name=name, items=items)
