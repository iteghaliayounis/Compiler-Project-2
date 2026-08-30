from flask import Flask, render_template

app = Flask(__name__)


# TEST 1 — يجب ألا يبلّغ: متغير محلي بدالة عادية (غير route)، مستخدم بدالة تانية
# غير مرتبطة إطلاقًا (كل دالة بايثون عندها scope مستقل تمامًا)
# ⚠️ قبل التصحيح: كان هاد التيست يطلّع UnboundLocalError غلط، بسبب bug حقيقي
# اكتشفته غالية بـ wasEverDefined() (كانت بتعتمد على allDefinedVars عالمي متراكم
# عبر كل دوال الملف، فأي اسم محلي بأي دالة، حتى لو بعيدة تمامًا، كان يخلي الفحص
# يعتقد إنو "معرّف بمكان ما" ويطلع ScopeError غلط). بعد التصحيح، ScopeChecker
# صار يقتصر بس على isAccessible ويسكت لما يفشل (شوفي TEST 10 لتوضيح ليش).


def helper():
    helper_value = 10
    return helper_value


@app.route("/use-after-helper")
def use_after_helper():
    print(helper_value)
    return "OK"



# TEST 2 — يجب ألا يبلّغ: نفس فكرة TEST 1 بس الاستخدام جوا if بالدالة اللاحقة


def helper_two():
    inner_total = 99
    return inner_total


@app.route("/use-inside-if")
def use_inside_if():
    condition = True
    if condition:
        print(inner_total)
    return "OK"



# TEST 3 — يجب ألا يبلّغ: متغير معرّف على مستوى الملف (global) قبل أي دالة


page_title = "My Store"


@app.route("/use-global")
def use_global():
    print(page_title)
    return "OK"



# TEST 4 — يجب ألا يبلّغ: متغير الـ for loop مستخدم بعد الحلقة بنفس الدالة
# (بايثون بتحتفظ بمتغير الحلقة بعد ما تخلص، والـ checker عندنا بيطابق هالسلوك)


@app.route("/loop-var-after")
def loop_var_after():
    total = 0
    for number in [1, 2, 3]:
        total = total + number
    print(number)
    return str(total)



# TEST 5 — يجب ألا يبلّغ: استخدام متغير بنفس الدالة اللي انعرّف فيها، بعد التعريف مباشرة


@app.route("/same-function")
def same_function():
    quantity = 5
    print(quantity)
    return str(quantity)



# TEST 6 — يجب ألا يبلّغ: متغير بنفس الاسم بدالتين route منفصلتين
# (route functions ما بينعمللها pop لل scope، فمتغيراتها بتضل "متاحة" حتى للـ route التانية،
#  هاي حالة موثقة بالكود نفسه ومش متأثرة بتصحيح Ghalia، مش خطأ بالتيست)


@app.route("/shared-a")
def shared_a():
    shared_name = "A"
    return shared_name


@app.route("/shared-b")
def shared_b():
    print(shared_name)
    return "OK"



# TEST 7 — يجب ألا يبلّغ: متغير استثناء (except ... as e) مستخدم بعد جسم except بنفس الدالة
# ⚠️ قبل التصحيح: كان هاد التيست كمان يطلّع UnboundLocalError غلط لـ 'e' — نفس جذر
# بگ TEST 1/2/9 بالضبط (fallback على SymbolTable العام، مش خطأ منفصل متعلق بـ except
# تحديدًا). بعد التصحيح ما عاد يبلّغ (صح، لأنو 'e' معرّفة بنفس الدالة وما تحذفت).


@app.route("/exception-var")
def exception_var():
    try:
        result = 10 / 2
    except Exception as e:
        result = None
    print(e)
    return str(result)



# TEST 8 — يجب ألا يبلّغ من ScopeChecker: متغير مش معرّف أبداً بأي مكان
# (هاي مسؤولية UndefinedVariableChecker مش ScopeChecker)


@app.route("/never-defined")
def never_defined():
    print(completely_unknown_name)
    return "OK"



# TEST 9 — يجب ألا يبلّغ: حالة ثانية مستقلة لتأكيد التصحيح (helper → route) بمتغير مختلف


def compute_discount():
    discount_rate = 0.1
    return discount_rate


@app.route("/second-case")
def second_case():
    print(discount_rate)
    return "OK"



# TEST 10 — الحالة الحقيقية الوحيدة لـ UnboundLocalError بلغة بايثون الفعلية:
# استخدام متغير قبل تعريفه بنفس الدالة (بايثون بتحسم وقت الترجمة إنو الاسم محلي
# للدالة كاملة لأنو معرّف بمكان ما بجسمها، حتى لو أسفل بالكود)
# متوقع: UnboundLocalError: local variable 'status' referenced before assignment
# ⚠️ ملاحظة: المصدر الفعلي لهاد الخطأ هو UseBeforeAssignmentChecker (تشيكر
# منفصل، تبع راما) مش ScopeChecker — ScopeChecker (بعد تصحيح بگ غالية) ما عاد
# يبلّغ عن هاد النمط نهائيًا تجنّبًا لتكرار نفس التقرير من تشيكرين مختلفين.


@app.route("/classic-unbound")
def classic_unbound():
    print(status)
    status = "active"
    return status


if __name__ == "__main__":
    app.run()
