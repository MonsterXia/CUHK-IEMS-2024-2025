# IEMS5710A Fall 2024- Assignment #2

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><br/>
</div>

[TOC]
<div style="page-break-after: always;"></div>
## 1. Hash Functions.

### (1) Assume that partial sha1 hashcode and partial password plaintext have been disclosed, find the complete password plaintext.

- Partial password: 1\*m\*-\*7\*o

- Partial hashcode: f4de626\*6792a8a\*f1b8154\*d448a5a\*ddc44f1\*

  Brute Force the answer by

```python
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
```

​	Which get

```
HASH = f4de62666792a8a0f1b81544d448a5aaddc44f1f
Password = 1Em5-S7Io
```

### (2) One server has experienced a data breach, exposing the hash codes of 50 users, as detailed in Problem1 md5 shadow.txt. The hash codes were generated using the md5 hash function, combining 8-digit passwords with random salt strings. Can you recover the original passwords?

```python
import hashlib

def read_shadow_file(filename):
    crack_list = []
    salt_list = []
    try:
        with open(filename, 'r') as f:
            for line in f:
                hash_value, salt = line.strip().split()
                crack_list.append(hash_value)
                salt_list.append(salt)
    except FileNotFoundError:
        print(f"Error: File {filename} not found")
        return []
    return crack_list, salt_list

def crack_password(hash, salt):
    for i in range(100000000):
        formatted_i = "{:08}".format(i)
        to_md5 = f"{formatted_i}" + salt
        md5_hash = hashlib.md5(to_md5.encode()).hexdigest()

        if md5_hash == hash:
            print(formatted_i)

filename = "Problem1_md5_shadow.txt"
crack_list, salt_list = read_shadow_file(filename)

for item in range(50):
    crack_password(crack_list[item], salt_list[item])

```

Get the password

```text
03957807
09035544
09956980
05323201
01290761
01425699
08325456
07718116
09809198
07670597
07566958
07517386
08462642
03347236
08810108
09157258
02594230
06983392
04657510
05230642
04140867
08911586
04067448
05409408
04359159
03450206
00771239
00977824
00755310
09263709
01441499
03697379
09605679
08657997
07660518
08296021
06893623
03162467
01333531
04522802
04817645
08874528
08422898
09103079
01410010
07880396
06673409
00466006
07392082
06890761
```

## 2. Buffer Overflow.

### (1) Explain why using gets() functions is vulnerable. How to eliminate this vulnerability?

The function gets() does not perform bounds checking, which leads to buffer overflow, since the user can put more characters  than buffer can hold.

Using gets() to specific the total number of input, ignore the others.

### (2) This program is vulnerable to a buffer overflow attack, allowing the user to grant permission without providing the correct password. Explain the reason for this vulnerability and demonstrate how to exploit it.

Just say, if the user put in 

```
IEMS5710IEMS5710
```

Without a '\0', the buffer will not end correctly, thus those input more than 8 characters will overflow and cover the original password's space. Let the overflow part same as buffer part, and then when the program check, it will find two are same, resulting in allowing the user to grant permission without providing the correct password.

### (3) backdoor() is located at address 0x41f. Construct a string that will jump the program into the function backdoor().

Let the input be 

```c
IEMS5710IEMS5710\x00\x00\x00\x00\x00\x00\x04\x1f
```

## 3. T/F Questions.

- (True/False) A TLS session may use more than one key when transmitting data from a client to a server.
    - True

- (True/False) Hashcode can be used to defense MITM attacks.
    - False

- (True/False) IPSec and SSL/TLS operate on the same layer in the OSI model.
    - False

- (True/False) A birthday attack is used to find the preimage of a given hash code.
    - False

