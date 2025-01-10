import scipy
import numpy as np
from PIL import Image
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
from sklearn.manifold import TSNE

# svd
np.set_printoptions(suppress=True)

A = np.array([
     [4, 3, 5, 4, 3, 5],
     [5, 4, 3, 1, 4, 2],
     [3, 1, 5, 5, 1, 5],
     [3, 4, 1, 3, 4, 3],
     [4, 3, 4, 5, 2, 5]
])

U,S,V = scipy.linalg.svd(A)
k=2
U_ = np.array(U[:,:k])
S_ = np.array(S[:k])
V_ = np.array(V[:k,:])

print("U=")
print(U_)
print("S=")
print(S_)
print("V=")
print(V_)

new = [5, 3, 4, 3, 5, 1]

result = np.dot(new, np.transpose(V_))
result = np.divide(result, S_)
print(result)

a = np.array([0.51595427, 0.09260778])
c = np.array([0.44836163, 0.58411878])

origin_a = np.array([4, 3, 5, 4, 3, 5])
origin_c = np.array([3, 1, 5, 5, 1, 5])

def cosSimilarity(a, b):
     return np.dot(a, b)/(np.linalg.norm(a)*np.linalg.norm(b))

print("Cosine Similarity:", cosSimilarity(a,c))
print("Original Cosine Similarity:", cosSimilarity(origin_a,origin_c))

# recommend system

recommend = np.array([
     [2, 1, 5, 4, 3, 0],
     [0, 2, 0, 3, 5, 4],
     [5, 0, 4, 1, 4, 2],
     [2, 3, 4, 5, 0, 0],
     [0, 4, 1, 0, 3, 2]
])

# item-item
average_item = np.mean(recommend, axis=0)

sum_item = np.zeros(recommend.shape[1])
for j in range(recommend.shape[1]):
    for i in range(recommend.shape[0]):
        sum_item[j] += (recommend[i, j]-average_item[j])**2

sum_item = np.sqrt(sum_item)

temp = np.zeros(recommend.shape[1])
for j in range(recommend.shape[1]):
    for i in range(recommend.shape[0]):
        temp[j] += (recommend[i, j] - average_item[j])*(recommend[i, 4] - average_item[4])

pearson_item = temp / (sum_item*sum_item[4])
print("Pearson item:", pearson_item)

sorted_indices = np.argsort(pearson_item)[::-1]
i2i = divisor = t = 0
for i in sorted_indices:
     if recommend[3][i] != 0:
          i2i += recommend[3][i] * pearson_item[i]
          divisor += pearson_item[i]
          t += 1
          if t == 2:
               break
i2i = i2i / divisor
print("i2i:", i2i)


# user-user
average_user = np.mean(recommend, axis=1)

sum = np.zeros(recommend.shape[0])
for i in range(recommend.shape[0]):
    for j in range(recommend.shape[1]):
        sum[i] += (recommend[i, j] - average_user[i])**2

sum = np.sqrt(sum)

temp = np.zeros(recommend.shape[0])
for i in range(recommend.shape[0]):
    for j in range(recommend.shape[1]):
        temp[i] += (recommend[i, j] - average_user[i])*(recommend[3, j] - average_user[3])

pearson_user = temp / (sum*sum[3])
print("Pearson User:", pearson_user)

sorted_indices = np.argsort(pearson_user)[::-1]
u2u = divisor = t = 0
for i in sorted_indices:
     if recommend[i][4] != 0:
          u2u += recommend[i][4] * pearson_user[i]
          divisor += pearson_user[i]
          t += 1
          if t == 2:
               break
u2u = u2u / divisor
print("u2u:", u2u)

# PCA

data_list = []
with open('train_img', 'r') as file:
    for i in range(768):
        line = file.readline()
        data_array  = np.array(line.split(','))
        data_list.append(data_array)
data_array = np.array(data_list)

U,S,V = scipy.linalg.svd(data_array)

lambda_k = 64

indices = np.arange(lambda_k)

plt.figure(figsize=(10, 6))
plt.plot(indices, np.array(S[:lambda_k]), marker='o', color='b', linestyle='-')
plt.xlabel('Index')
plt.ylabel('Eigenvalue')
plt.title('Visualization of Eigenvalues ({})'.format('Your Student ID'))
plt.grid(True)
plt.savefig('Eigenvalues_EigenDigits.png')
plt.show()
plt.close()

# read dataset
def read_from_file(filename):
    res = []
    with open(filename, 'r') as f:
        for line in f:
            row = [int(elem) for elem in line.strip().split(',')]
            res.append(row)
    res = np.array(res)
    return res

data = read_from_file('train_img')
label = read_from_file('train_label')
seed = read_from_file('random_seed_1')

pca = PCA(n_components=8)
merged_arr = np.vstack((data, seed))
test = pca.fit_transform(merged_arr)
data_new = test[:60000]
seed_new = test[60000:]

def k_means_PCA(data, label, seed, k, detail=None, output=None) :
    if output is None:
        output = []
    if detail is None:
        detail = []
    try:
        if k>0 & k < len(data[0]) :
            pca = PCA(n_components=k)
            merged_arr = np.vstack((data, seed))
            temp = pca.fit_transform(merged_arr)
            data_new = temp[:60000]
            seed_new = temp[60000:]

        elif k == len(data[0]):
            data_new = data
            seed_new = seed
        else:
            raise ValueError("k must 0 < k < len(image)")
    except ValueError as ve:
        print("ValueError Exception：", ve)
    except Exception as e:
        print("Exception：", e)

    estimator = KMeans(
        n_clusters=10,
        init=seed_new,
    )

    estimator.fit(data_new)
    label_pred = estimator.predict(data_new)

    centroids = estimator.cluster_centers_
    np.savetxt(f"CenterPoints/pca_components_{k}", centroids, fmt='%.8f', delimiter=',', newline='\n')

    tsne = TSNE(n_components=2, random_state=0)
    low_dim_rep = tsne.fit_transform(data_new)
    plt.figure(figsize=(8, 6))
    plt.scatter(low_dim_rep[:, 0], low_dim_rep[:, 1], c=label_pred, cmap='viridis')
    plt.title(f'Distribution of points where Eigen Digit = {k} (Your Student ID)')
    plt.savefig(f'Distribute/{k}.png')
    plt.show()
    plt.close()

    detail.append(f"EigenDigits = {k}:")
    dictionary = {}
    correct = 0
    count = [[0 for _ in range(10)] for _ in range(10)]
    for i in range(len(label_pred)):
        dictionary[label_pred[i]] = dictionary.get(label_pred[i], 0) + 1
        count[label_pred[i]][label[i][0]] += 1

    for key, value in dictionary.items():
        right = max(count[key])
        correct += right
        tag = count[key].index(right)

        detail.append(f"Cluster {key}: {tag}, {right}, {value}, {100*right / value}%")

    detail.append(f"Accuracy for eigen digits = {k}: {100 * correct / len(label_pred)}%")
    output.append(f"Accuracy for eigen digits = {k}: {100 * correct / len(label_pred):.2f}%")

    return detail, output

detail = []
output = []

for i in (4, 8, 16, 32, 64, 784):
    detail, output = k_means_PCA(data, label, seed, i, detail, output)

x = []
y = []
for item in output:
    parts = item.split(':')
    x_val = int(parts[0].split('=')[-1].strip())
    y_val = float(parts[1].replace('%', '').strip())
    x.append(x_val)
    y.append(y_val)

plt.figure(figsize=(8, 5))
plt.plot(x, y, marker='o', color='b', linestyle='-')

plt.xscale('log', base=2)

for i in range(len(x)):
    plt.text(x[i], y[i], f'({x[i]}, {y[i]})', ha='left', va='bottom')

plt.title('Visualization of Eigen Digits ({})'.format('Your Student ID'))
plt.xlabel('Eigen Digits')
plt.ylabel('Accuracy (%)')

plt.grid(True)
plt.savefig('Accuracy_EigenDigits.png')
plt.show()
plt.close()

image_dir = "Distribute/"
image_files = ['4.png', '8.png', '16.png', '32.png', '64.png', '784.png']

fig, axes = plt.subplots(2, 3, figsize=(12, 8))
for i, filename in enumerate(image_files):
    image = Image.open(image_dir + filename)
    row = i // 3
    col = i % 3
    axes[row, col].imshow(image)
    axes[row, col].axis('off')

    image.close()

plt.tight_layout()
plt.suptitle('Visualization of Distributions ({})'.format('Your Student ID'))
plt.savefig('Distribute/merged_image.png')
plt.show()
plt.close()



