# Homework 4

[TOC]

## Statement
I declare that the assignment submitted on Elearning system is original except for source material explicitly acknowledged, and that the same or related material has not been previously submitted for another course. I also acknowledge that I am aware of University policy and regulations on honesty in academic work, and of the disciplinary guidelines and procedures applicable to breaches of such policy and regulations, as contained in the website [https://www.cuhk.edu.hk/policy/academichonesty/](https://www.cuhk.edu.hk/policy/academichonesty/).

<div align=center style="font-size: 18px;">
    Name<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>Your Name</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>SID<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>1155xxxxxx</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><br/>Date<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u><u>29/11/2024</u><u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>Signature <u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>
</div>





<div style="page-break-after: always;"></div>
## Q1 [20 marks]: Singular Value Decomposition (SVD) for Dimensionality Reduction

### a. [10 marks] 

Use spicy to get SVD

```python
import scipy
import numpy as np

np.set_printoptions(suppress=True)

A = np.array([
     [4, 3, 5, 4, 3, 5],
     [5, 4, 3, 1, 4, 2],
     [3, 1, 5, 5, 1, 5],
     [3, 4, 1, 3, 4, 3],
     [4, 3, 4, 5, 2, 5]])

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
```

Which get( $k = 2$ )

```
U=
[[ 0.51595427  0.09260778]
 [ 0.38655583 -0.63604974]
 [ 0.44836163  0.58411878]
 [ 0.36560369 -0.45212343]
 [ 0.49967146  0.20311125]]
S=
[19.29555729  5.67822901]
V=
[[ 0.43726014  0.33706603  0.43251078  0.42949521  0.31117036  0.47626816]
 [-0.28202218 -0.50744991  0.32330594  0.40754983 -0.54322009  0.31184358]]
```

###  b. [10 marks] 

#### 1. [5 marks]

Based on a, add codes

```python
new = [5, 3, 4, 3, 5, 1]

result = np.dot(new, np.transpose(V_))
result = np.divide(result, S_)
print(result)
```

Get

```
[ 0.42746355 -0.4967824 ]
```

#### 2. [5 marks]

Calculate the similarities by

```python
a = np.array([0.51595427, 0.09260778])
c = np.array([0.44836163, 0.58411878])

origin_a = np.array([4, 3, 5, 4, 3, 5])
origin_c = np.array([3, 1, 5, 5, 1, 5])

def cosSimilarity(a, b):
     return np.dot(a, b)/(np.linalg.norm(a)*np.linalg.norm(b))

print("Cosine Similarity:", cosSimilarity(a,c))
print("Original Cosine Similarity:", cosSimilarity(origin_a,origin_c))
```

Get

```
Cosine Similarity: 0.7394540474185124
Original Cosine Similarity: 0.948928404190258
```

## Q2 [50 marks]: K-means with PCA and Eigendigits

### a. [35 marks]

Select 768 images to get the Eigenvalues by

```python
import scipy
import numpy as np
import matplotlib.pyplot as plt

data_list = []
with open('train_img', 'r') as file:
    for i in range(768):
        line = file.readline()
        data_array  = np.array(line.split(','))
        data_list.append(data_array)
data_array = np.array(data_list)

U,S,V = scipy.linalg.svd(data_array)

lemda = 64

indices = np.arange(lemda)

plt.figure(figsize=(10, 6))
plt.plot(indices, np.array(S[:lemda]), marker='o', color='b', linestyle='-')
plt.xlabel('Index')
plt.ylabel('Eigenvalue')
plt.title('Visualization of Eigenvalues ({})'.format('Your Student ID'))
plt.grid(True)
plt.show()
```

Which get

Image Here



It can be clearly seen that the eigenvalues rapidly decay from large to small, which means that the main energy is concentrated in the head, indicating a smaller contribution from the long tail portion. Selecting only the head can roughly reflect the information of the original matrix, providing a possibility for compression.

Do the PCA for the dataset and seed to preprocessing the dataset

```python
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
```

Do the K-Means in the MapReduce and get the output, which size are like(e.g. $k=4$ )

```
28.44302328,229.96716177,-328.79352485,533.94743538
-341.78481336,-596.95481366,138.91946839,341.24250884
1190.61853259,226.19474479,-191.79778723,212.85865870
-488.38965183,28.16507015,100.81126685,-105.24617098
258.33442223,448.82623944,607.68276325,145.07395613
-45.25323380,-656.46273764,-395.25613160,217.51531204
435.26677636,-243.64855147,58.56385347,-781.26874599
195.27639314,160.98496572,-515.80657609,-420.94094497
38.95523617,-642.69243718,450.50747471,-188.83956888
-782.52696071,480.33892400,-150.45666614,-113.82287273
```

Use the totally accuracy(already written in HW3), together with k, draw the accuracy

```python
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
```

Get

Image Here

With a smaller number of selected eigenvalues, the accuracy fluctuates significantly. However, when selecting $2^4$ eigenvalues, the overall accuracy tends to stabilize, indicating that the 784-dimensional vector is approximately compressed to 16 dimensions in this case. It is also worth noting that the accuracy is relatively high when using 8 dimensions, suggesting that the initial value selection for clustering may not be optimal. Mapping to 8 dimensions helps avoid many local minima that are not global minima during the optimization process. There are still some techniques that can further improve the accuracy.

### b. [15 marks]

By using the center points saved, get the predict label, and the use TSNE to draw the every distribution

```python
tsne = TSNE(n_components=2, random_state=0)
low_dim_rep = tsne.fit_transform(data_new)
plt.figure(figsize=(8, 6))
plt.scatter(low_dim_rep[:, 0], low_dim_rep[:, 1], c=label_pred, cmap='viridis')
plt.title(f'Distribution of points where Eigen Digit = {k} (Your Student ID)')
plt.savefig(f'Distribute/{k}.png')
plt.show()
plt.close()
```

Use those png to form a complete picture by

```python
from PIL import Image
import matplotlib.pyplot as plt

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
```

Which get

Image Here

From the graph, it can be observed that when the compressed dimension is greater than $2^4$, for cases with obvious boundaries and large gaps between the boundaries, the two classes can be clearly distinguished. This is consistent with the conclusion drawn from the line graph as well.

## Q3 [30 marks]: Recommender Systems

### a. [10 marks]

#### i. [5 marks] # item-item

```python
import numpy as np

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
```

Result:

```
Pearson item: [-0.06520507 -0.42257713 -0.49311367 -0.51553926  1.          0.79859571]
i2i: 2.8663233975998086
```

#### ii. [5 marks] # user-user

```python
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
```

Result:

```
Pearson User: [ 0.621059   -0.546875   -0.31185278  1.         -0.43481318]
u2u: 1.9914408169744053
```

### b. [10 marks] Model-based Collaborative Filtering.

The code initializes two matrices randomly and iteratively calculates the product of one matrix with the transpose of the other matrix. It compares this product with the original matrix, computes the gradients, and updates both matrices accordingly. This process aims to make the product of these two matrices (one of which is transposed) gradually approach the original matrix. The update continues until the difference between the product and the original matrix is less than a threshold, at which point the iteration stops. Finally, the code outputs these two matrices, and externally, the product is computed and printed to display the final estimated matrix.

Get

```
[[ 2.09219008  0.99067742  4.82778027  4.00985151  3.04486826  3.5414268 ]
 [ 4.01009516  1.89872852  4.74222166  3.32710075  4.99173778  3.67918622]
 [ 4.94229174 -0.17575597  3.97583317  0.83657892  3.9492839   2.25896684]
 [ 1.903653    3.12830515  4.1910623   4.74456716  4.37299568  3.95760961]
 [-0.1190516   3.91265453  0.90118251  3.20871769  2.97025457  2.16702677]]
```

So that

```
4.37299568
```

Compared to question a, the recommended value is much larger, but for user 4, there is a significant discrepancy between the other values in the generated matrix and the actual given value. Some values have offsets that can exceed 0.35, which means the maximum possible error could be up to 0.7.

### c. [10 marks] Bug Correction.

In updating the Gradient, the original code used the updated value to calculate when updating the second matrix, that is incorrect in theoretic though it may do better in practice somehow. The correct step to do is like following code. In order to address the issue of getting stuck in local minima rather than reaching the global minimum during optimization, alternative methods such as the Adam optimizer or approaches that consider multiple initial points can be used. Instead of employing incorrect gradient update methods, the original code would lead to slow optimization speeds and a sharp increase in training costs.

```python
# old version
# P[i][k] = P[i][k] + alpha * (2 * eij * Q[k][j] - beta * P[i][k])
# Q[k][j] = Q[k][j] + alpha * (2 * eij * P[i][k] - beta * Q[k][j])
# new
P_old = P[i][k]
Q_old = Q[k][j]
P[i][k] = P_old + alpha * (2 * eij * Q_old - beta * P_old)
Q[k][j] = Q_old + alpha * (2 * eij * P_old - beta * Q_old)
```

Get the whole matrix

```
[[ 2.07409704  0.94629733  4.89601033  4.06906281  2.96354073  2.1235133 ]
 [ 5.15795335  2.05020759  4.56562051  3.03747961  5.20564516  3.55588337]
 [ 4.94057046 -0.30212971  3.99285166  0.9592597   3.79810433  2.3765005 ]
 [ 1.94580686  3.04962527  4.0806985   4.88815358  3.68159732  2.790595  ]
 [ 1.28820438  3.89396798  0.95720155  2.99190003  2.87428096  2.2670144 ]]
```

So that

```
3.68159732
```

The corrected value obtained was originally less than the value obtained from question b, so it can be inferred that the two obtained values are not from the same local minimum point. Therefore, to compare the two methods, it is best to train multiple times. It is observed that after correction, the distribution variance of this value is smaller, indicating increased accuracy.

## References

<ol>
    <li>https://mobitec.ie.cuhk.edu.hk/iems5709Fall2024/</li>
  	<li>https://scikit-learn.org/stable/modules/generated/sklearn.manifold.TSNE.html</li>
  	<li>https://scikit-learn.org/dev/modules/generated/sklearn.decomposition.PCA.html</li>
  	<li>https://docs.scipy.org/doc/scipy/reference/generated/scipy.linalg.svd.html</li>
</ol>
