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