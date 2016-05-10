def eqLists(a, b) :
    if len(a) != len(b) :
        return False
    for x, y in zip(a, b) :
        if x != y :
            return False
    return True

def compLists(a, b) :
    for x, y in zip(a, b) :
        if x < y :
            return -1
        if x > y :
            return 1
    if len(a) < len(b) :
        return -1
    if len(a) > len(b) :
        return 1
    return 0

"""
list1 = [1,2,3,4]
list2 = [1,2]
list3 = [123,111]

def intersect_sorted(a, b) :
    matches = []
    ia, ib = 0, 0
    la, lb = len(a), len(b)
    while ia < la and ib < lb :
        va, vb = a[ia], b[ib]
        if va < vb :
            ia += 1
        elif va > vb :
            ib += 1
        else :
            matches.append(va)
            ia += 1
            ib += 1
     return matches

for i in range(len(list1)) :
    if list1[i] > 2 :
        print list1[i]
        print "i = " + str(i)
        i = i + 2
        print "i = " + str(i)
        """

"""listx = [list1, list2, list3]
print sorted(listx) """
