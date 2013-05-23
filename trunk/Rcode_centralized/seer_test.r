# code for running partial SEER data with each event time contain more than one observations
# seer_test.txt data feathure z is col 1:20, time t is col 21, indicator delta is col 22. 

#coefficient estimation for Cox model is as follows
#              [,1]
# [1,]  0.046623845
# [2,]  0.165579422
# [3,] -0.080604158
# [4,] -0.046697784
# [5,]  0.427327063
# [6,]  0.305083819
# [7,]  0.468387444
# [8,] -1.557172074
# [9,] -0.200149951
#[10,] -0.281987847
#[11,] -0.986807684
#[12,] -0.898649456
#[13,] -0.624711717
#[14,] -0.076431276
#[15,]  0.058970363
#[16,]  0.034870907
#[17,]  0.007656108
#[18,]  0.006768794
#[19,]  0.643476195
#[20,] -0.164379343


full_data<-as.matrix(read.table("C:/Users/privacy/Dropbox/DistributedCOX_new/code/seer_test.txt",sep="\t"))	

z<-full_data[,1:20]
t<-full_data[,21]
delta<-full_data[,22]

full_n<-dim(z)[1]
m<-dim(z)[2]

# sorting oberserved data according to accending order of t
z<-z[order(t),]
t<-t[order(t)]
delta<-delta[order(t)]

zz<-array(0,c(full_n,m,m))

for(i in 1:m)
{
	for(j in 1:m)
		{
			zz[,i,j]<-z[,i]*z[,j]
		}
}

# based on the partial likelihood function, newton method needs some quantities for all unique time points
unique_t<-unique(t)
n<-length(unique_t)
s<-matrix(0,n,m)
d<-rep(0,n)
index<-rep(0,n)

############# this part should be improved by dropping the loops#####################
for(i in 1:n)
{
    if (length(t[t==unique_t[i]&delta==1])>1)
		s[i,]<-colSums(z[t==unique_t[i]&delta==1,])
	if (length(t[t==unique_t[i]&delta==1])==1)
		s[i,]<-z[t==unique_t[i]&delta==1,]
	if (length(t[t==unique_t[i]&delta==1])==0)
	    s[i,]<-rep(0,m)
}

for(i in 1:n)
{
    d[i]<-length(t[t==unique_t[i]&delta==1])
}

for(i in 1:n)
{
	index[i]<-length(t[t<unique_t[i]])+1
}
########################################################################################
# newton method starts
beta_old<-matrix(rep(-1,m),m,1)
beta<-matrix(rep(0,m),m,1)
k<-0

while(abs(sum(beta-beta_old))>10^-6&k<20)
{
	beta_old<-beta
	temp1<-exp(z%*%beta_old)
	temp1<-c(temp1)
	temp2<-rev(temp1)
	temp2<-cumsum(temp2)
	temp2<-rev(temp2)

	sum_matrix<-z*temp1
	sum_matrix<-apply(sum_matrix,2,rev)
	sum_matrix<-apply(sum_matrix,2,cumsum)
	sum_matrix<-apply(sum_matrix,2,rev)
	sum_matrix<-sum_matrix/temp2
	sum_matrix<-sum_matrix[index,]

	gradient<-colSums(s-sum_matrix*d)
	
	# gradient for partial likelihood function
	gradient<-matrix(gradient,m,1)

	sum_array<-zz*temp1
	sum_array<-apply(sum_array,c(2,3),rev)
	sum_array<-apply(sum_array,c(2,3),cumsum)
	sum_array<-apply(sum_array,c(2,3),rev)
	sum_array<-sum_array/temp2
	sum_array<-sum_array[index,,]

	for(i in 1:m)
	{
		for(j in 1:m)
		{
			sum_array[,i,j]<-sum_array[,i,j]-sum_matrix[,i]*sum_matrix[,j]
		}
	}

	neghessian<-sum_array*d
	
	# Hessian matrix for partial likelihood function
	neghessian<-apply(neghessian,c(2,3),sum)
	# newton iteration
	beta<-beta_old+solve(neghessian+diag(10^(-6),m))%*%gradient
	k<-k+1
}
beta
exp(beta)

# cox PH model R package
library(splines)
library(survival)
fit <- coxph(Surv(t, delta) ~ z[,1] +z[,2] +z[,3] +z[,4] +z[,5] +z[,6] +z[,7] +z[,8] +z[,9] +z[,10] +z[,11] +z[,12] +z[,13] +z[,14]  +z[,15] +z[,16] +z[,17] +z[,18]+z[,19] +z[,20],method="breslow")
fit
