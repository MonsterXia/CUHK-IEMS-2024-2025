# IEMS5710A Fall 2024- Assignment #1

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Name Here</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><br/>
</div>

[TOC]
<div style="page-break-after: always;"></div>
## 1. Consider the non-shadowed entry in a Linux machine below.

```
 sha256:$1$sha512$HtQTtdEaQhsBlwP2gJZpO/:500:600:::
```

- a) What is the username?
    - sha256
- What is the hash/encryption method used?
    - sha512
- What is the salt used?
    - HtQTtdEaQhsBlwP2gJZpO/

## 2. Suppose we are going to encrypt a 10-byte file using AES128 in CBC mode, padding with PKCS#7.

- What is the ciphertext size?
    - 128 bits(16 Byte)

- How many padding bytes are there?
    - 6 Byte

- What is the content of each padding byte?
    - <0x06> <0x06> <0x06> <0x06> <0x06> <0x06>

## 3. Is there a full period for each of the following LCGs? Explain your answer.

- a) m=32,a=9,c=0 
    - No，c must be positive integer if m is not a prime，or just to say if $m = 2^n, c = 0$ has maximal period *m*/4.

- b) m=32,a=9,c=2 
    - No, $gcd(c, m) = 2 \neq 1$.

- c) m=32,a=9,c=3
    - Yes.

## 4. An attacker eavesdropped a public key file in PKCS#1 format along with ciphertext encrypted using the RSA algorithm. Try to decode the plaintext, and show your steps. (Hint: Use openssl rsa -pubin -in public.pem -text -noout to parse the public key file)

public.pem:

```
-----BEGIN PUBLIC KEY----
MCQwDQYJKoZIhvcNAQEBBQADEwAwEAIJBWvHrU2oMxbbAgMBAAE=
-----END PUBLIC KEY---
```

Ciphertext:

```
AxMh6FqZ4mdV
```

Use command to get the public key info

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411100458319.png"/>

- $N$ : Modulus: 056bc7ad4da83316db
- $N = 100000087000017475291$ 
- $p = 10000003147$ 
- $q = 10000005553$
- $\phi(N) = (p-1)(q-1) = 100000086980017466592$
- $e = 65537$
- $d = 10854946347495983297$ 

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411101937558.png"/>

For c = "AxMh6FqZ4mdV", convert to integer: $c = 56718870657907189589$

Thus $m \equiv_{N} c^d = 5279711157932208432$, switch to string: 

```
IEMS5710
```

## 5. Assume that Alice encrypted a bit string by the following steps:

- Use plaintext as the seed to set up a Linear Congruential Generator (LCG);
-  Use $Z_{10}$ generated from the LCG as the ciphertext

However, Eve knows the parameters of the LCG and eavesdropped on the ciphertext. Can Eve decrypt the message? Show the steps. (Hint: you can use python library Crypto.Util.number.long_to_bytes to check the message)

Eavesdropped Parameters:

```
a=			2815675175253318914878108460948169305201889736892014759387029406311167
c=			1904728121096264384293052023573590678799868915696638582430846369537791
m=			1984022522177509005484138128176773942914583859539906313397324398933453
ciphertext=	1715610578739814070001774693311884433646613212955777636517269434000229
```

$gcd(a,m) = 1$, which means $ax \equiv_M ciphertext - c$ has and only has one solution: $x \equiv_M (ciphertext - c)*a^{-1}$

For $Z_{10}$, do it for 10 times.

Thus: $x = 1975374464499752957022836334385475210854849593148633001042942270128967$

Which turned out to be

```
IEMS5710_set_good_seed_to_LCG
```

<img src="https://cdn.jsdelivr.net/gh/MonsterXia/Piclibrary/Pic202411102258602.png"/>
