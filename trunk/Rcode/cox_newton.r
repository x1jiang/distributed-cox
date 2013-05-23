############ Newton method for Cox proportional hazard model MLE based on Breslow's Partial likelihood #####################
# t is censored or uncensored time
# delta is indicator function, 1 means uncensored, 0 means censored
# z is the covariate matrix, row number is feature number 
# cumsum() rev() and apply() are used for matrix or array operations

full_n<-2000
m<-5
z<-rnorm(full_n*m,0,1)
z<-matrix(z,full_n,m)

beta0<-matrix(rep(1,m),m,1)

u<-runif(full_n,0,1)
# generate proportional hazard survival data with exponential baseline
t<-(3.9)*(-log(u)*c(exp(-z%*%beta0)))

# censoring time
c<-runif(full_n,0,1)

# indicator function, 1 means uncensored, 0 means right censored
delta<-rep(0,full_n)
delta[t<c]<-1

# sorting oberserved data according to accending order of t
z<-z[order(t),]
delta<-delta[order(t)]
t<-t[order(t)]

m<-dim(z)[2]
full_n<-length(t)

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



fit <- coxph(Surv(t, delta) ~ z[,1] +z[,2] +z[,3]+z[,4]+z[,5], method="breslow")