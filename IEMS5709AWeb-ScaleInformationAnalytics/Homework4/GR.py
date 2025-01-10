import numpy
from joblib.numpy_pickle_utils import xrange

def matrix_factorization(R, P, Q, K, steps=5000, alpha=0.0002, beta=0.02):
    Q = Q.T
    for step in xrange(steps):
        for i in xrange(len(R)):
            for j in xrange(len(R[i])):
                if R[i][j] > 0:
                    eij = R[i][j] - numpy.dot(P[i,:],Q[:,j])
                    for k in xrange(K):
                        P_old = P[i][k]
                        Q_old = Q[k][j]
                        P[i][k] = P_old + alpha * (2 * eij * Q_old - beta * P_old)
                        Q[k][j] = Q_old + alpha * (2 * eij * P_old - beta * Q_old)
        eR = numpy.dot(P,Q)
        e = 0
        for i in xrange(len(R)):
            for j in xrange(len(R[i])):
                if R[i][j] > 0:
                    e = e + pow(R[i][j] - numpy.dot(P[i,:],Q[:,j]), 2)
                    for k in xrange(K):
                        e = e + (beta/2) * (pow(P[i][k],2) + pow(Q[k][j],2))
        if e < 0.001:
            break
    return P, Q.T

R = [
     [2, 1, 5, 4, 3, 0],
     [0, 2, 0, 3, 5, 4],
     [5, 0, 4, 1, 4, 2],
     [2, 3, 4, 5, 0, 0],
     [0, 4, 1, 0, 3, 2]
]

R = numpy.array(R)

N = len(R)
M = len(R[0])
K = 3

P = numpy.random.rand(N,K)
Q = numpy.random.rand(M,K)

nP, nQ = matrix_factorization(R, P, Q, K)
nR = numpy.dot(nP, nQ.T)

print(nR)

