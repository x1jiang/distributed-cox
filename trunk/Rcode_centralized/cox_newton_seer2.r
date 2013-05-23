############ Newton method for Cox proportional hazard model MLE based on Breslow's Partial likelihood #####################
# t is censored or uncensored time
# delta is indicator function, 1 means uncensored, 0 means censored
# z is the covariate matrix, row number is feature number 
# cumsum() rev() and apply() are used for matrix or array operations

full_data<-as.matrix(read.table("C:/Users/yuan/Dropbox/DistributedCOX/code/seer.txt",sep="\t"))	
full_n<-dim(full_data)[1]
add<-full_data[,1]

race<-full_data[,2]
race[race>=3&race<=97]<-3
race2<-rep(0,full_n)
race2[race==2]<-1
race3<-rep(0,full_n)
race3[race==3]<-1
race98<-rep(0,full_n)
race98[race==98]<-1
race99<-rep(0,full_n)
race99[race==99]<-1

marital<-full_data[,3]
mar2<-rep(0,full_n)
mar2[marital==2]<-1
mar3<-rep(0,full_n)
mar3[marital==3]<-1
mar4<-rep(0,full_n)
mar4[marital==4]<-1
mar5<-rep(0,full_n)
mar5[marital==5]<-1
mar9<-rep(0,full_n)
mar9[marital==9]<-1

histology<-full_data[,4]
hist8520<-rep(0,full_n)
hist8520[histology==8520]<-1
hist8522<-rep(0,full_n)
hist8522[histology==8522]<-1
hist8480<-rep(0,full_n)
hist8480[histology==8480]<-1
hist8501<-rep(0,full_n)
hist8501[histology==8501]<-1
hist8201<-rep(0,full_n)
hist8201[histology==8201]<-1
hist8211<-rep(0,full_n)
hist8211[histology==8211]<-1

grade<-full_data[,5]

ts<-full_data[,6]

nne<-full_data[,7]

npn<-full_data[,8]



er<-full_data[,12]
er2<-rep(0,full_n)
er2[er==2]<-1
er3<-rep(0,full_n)
er3[er==3]<-1
er4<-rep(0,full_n)
er4[er==4]<-1

t<-full_data[,9]+full_data[,10]*12
delta<-full_data[,11]
delta[delta==1]<-0
delta[delta==4]<-1

z<-cbind(add,race2,race3,mar2,mar3,mar4,mar5,mar9,hist8520,hist8522,hist8480,hist8501,hist8201,hist8211,grade,ts,nne,npn,er2,er4)

z<-z[race98==0&race99==0&er3==0,]
t<-t[race98==0&race99==0&er3==0]
delta<-delta[race98==0&race99==0&er3==0]

full_n<-dim(z)[1]

random_s<-sample(1:full_n)
t<-t[random_s]
delta<-delta[random_s]
z<-z[random_s,]


random_s<-sample(1:full_n)
t<-t[random_s]
delta<-delta[random_s]
z<-z[random_s,]

t_keep<-t
delta_keep<-delta
z_keep<-z
#8005700631

t1<-t_keep[1:250]
delta1<-delta_keep[1:250]
z1<-z_keep[1:250,]


t2<-t_keep[251:full_n]
delta2<-delta_keep[251:full_n]
z2<-z_keep[251:full_n,]

z<-z1
t<-t1
delta<-delta1

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

# suvival curve plotting
index_uncensor<-index[d>0]
d_uncensor<-d[d>0]

beta_hat<-beta
temp1<-exp(z%*%beta_hat)
temp1<-c(temp1)
temp2<-rev(temp1)
temp2<-cumsum(temp2)
temp2<-rev(temp2)
temp2<-temp2[index_uncensor]

rate<-d_uncensor/temp2

n<-length(index_uncensor)


survival1<-rep(0,n)
temp<-0
for(i in 1:n)
{
	temp<-temp+rate[i]
	survival1[i]<-exp(-temp)
}


y<-rep(0,full_n)
mse<-0
for(i in 1:n)
{
	y[t==t[index_uncensor[i]]&delta==1]<-1
	p<-(survival1[i])^(exp(z%*%beta_hat))
	p[delta==0&t<t[index_uncensor[i]]]<-1
	mse<-mse+mean((y-(1-p))^2)
}
mse1<-mse/n


survival1<-survival1^(exp(z_keep[2,]%*%beta_hat))

t1<-t[index_uncensor]
n<-length(t1)
sur1<-rep(0,2*n-1)
for(i in 1:(n-1))
{
	sur1[2*i-1]<-survival1[i]
	sur1[2*i]<-survival1[i]
}
sur1[2*n-1]<-survival1[n]

tt1<-rep(0,2*n-1)
tt1[1]<-t1[1]
for(i in 1:(n-1))
{
	tt1[2*i]<-t1[i+1]
	tt1[2*i+1]<-t1[i+1]
}

#################################################################################################################################
z<-z2
t<-t2
delta<-delta2

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

# suvival curve plotting
index_uncensor<-index[d>0]
d_uncensor<-d[d>0]

beta_hat<-beta
temp1<-exp(z%*%beta_hat)
temp1<-c(temp1)
temp2<-rev(temp1)
temp2<-cumsum(temp2)
temp2<-rev(temp2)
temp2<-temp2[index_uncensor]

rate<-d_uncensor/temp2

n<-length(index_uncensor)


survival2<-rep(0,n)
temp<-0
for(i in 1:n)
{
	temp<-temp+rate[i]
	survival2[i]<-exp(-temp)
}


y<-rep(0,full_n)
mse<-0
for(i in 1:n)
{
	y[t==t[index_uncensor[i]]&delta==1]<-1
	p<-(survival2[i])^(exp(z%*%beta_hat))
	p[delta==0&t<t[index_uncensor[i]]]<-1
	mse<-mse+mean((y-(1-p))^2)
}
mse2<-mse/n

survival2<-survival2^(exp(z_keep[2,]%*%beta_hat))

t2<-t[index_uncensor]


n<-length(t2)
sur2<-rep(0,2*n-1)
for(i in 1:(n-1))
{
	sur2[2*i-1]<-survival2[i]
	sur2[2*i]<-survival2[i]
}
sur2[2*n-1]<-survival2[n]

tt2<-rep(0,2*n-1)
tt2[1]<-t2[1]
for(i in 1:(n-1))
{
	tt2[2*i]<-t2[i+1]
	tt2[2*i+1]<-t2[i+1]
}










#######################################################################################################################
z<-z_keep
t<-t_keep
delta<-delta_keep

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

# suvival curve plotting
index_uncensor<-index[d>0]
d_uncensor<-d[d>0]

beta_hat<-beta
temp1<-exp(z%*%beta_hat)
temp1<-c(temp1)
temp2<-rev(temp1)
temp2<-cumsum(temp2)
temp2<-rev(temp2)
temp2<-temp2[index_uncensor]

rate<-d_uncensor/temp2

n<-length(index_uncensor)


survival3<-rep(0,n)
temp<-0
for(i in 1:n)
{
	temp<-temp+rate[i]
	survival3[i]<-exp(-temp)
}


# mse computating

y<-rep(0,full_n)
mse<-0
for(i in 1:n)
{
	y[t==t[index_uncensor[i]]&delta==1]<-1
	p<-(survival3[i])^(exp(z%*%beta_hat))
	p[delta==0&t<t[index_uncensor[i]]]<-1
	mse<-mse+mean((y-(1-p))^2)
}
mse3<-mse/n


survival3<-survival3^(exp(z_keep[2,]%*%beta_hat))

t3<-t[index_uncensor]



sur3<-rep(0,2*n-1)
for(i in 1:(n-1))
{
	sur3[2*i-1]<-survival3[i]
	sur3[2*i]<-survival3[i]
}
sur3[2*n-1]<-survival3[n]

tt3<-rep(0,2*n-1)
tt3[1]<-t3[1]
for(i in 1:(n-1))
{
	tt3[2*i]<-t3[i+1]
	tt3[2*i+1]<-t3[i+1]
}


#png("C:/Users/yuan/Desktop/test_25000.png",height=500,width=500)
#plot(c(1, 107),c(0.87,1),type='n', xlab='time', ylab='survival',cex.lab=1.2)
#lines(tt1,sur1,lty=3,col=2)
#lines(tt2,sur2,lty=2,col=3)
#lines(tt3,sur3,lty=1,col=4)
#legend(10, 0.90, c("data of size 25000", "data of size 29753", "combined data"), lty=3:1, col=2:4)
#dev.off()

wilcox.test(survival1,survival3)

wilcox.test(survival2,survival3)