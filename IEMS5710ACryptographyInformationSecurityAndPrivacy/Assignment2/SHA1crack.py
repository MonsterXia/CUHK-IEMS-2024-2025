import hashlib

partial_password = "1*m*-*7*o"
partial_hashcode = "f4de626*6792a8a*f1b8154*d448a5a*ddc44f1*"

def compare(pswd ,str2):
    flag = True
    for i in range(len(pswd)):
        if pswd[i] != '*':
            if pswd[i] != str2[i]:
                flag = False
    return flag

for i in range(32, 127):
    temp1 = partial_password.replace('*', chr(i), 1)
    for j in range(32, 127):
        temp2 = temp1.replace('*', chr(j), 1)
        for k in range(32, 127):
            temp3 = temp2.replace('*', chr(k), 1)
            for l in range(32, 127):
                temp4 = temp3.replace('*', chr(l), 1)

                sha1_hash = hashlib.sha1()
                sha1_hash.update(temp4.encode('utf-8'))
                hashed_result = sha1_hash.hexdigest()

                if compare(partial_hashcode, hashed_result):
                    print("HASH = " + hashed_result)
                    print("Password = " + temp4)


