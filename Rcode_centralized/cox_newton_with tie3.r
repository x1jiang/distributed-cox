############ Newton method for Cox proportional hazard model MLE based on Breslow's Partial likelihood #####################
# t is censored or uncensored time
# delta is indicator function, 1 means uncensored, 0 means censored
# z is the covariate matrix, row number is feature number 
# cumsum() rev() and apply() are used for matrix or array operation
run<-100
full_n<-1000
m<-3
para_dis<-matrix(0,m,run)
para_rep<-matrix(0,m,run)
for (rr in 1:run)
{
	z<-rnorm(full_n*m,0,1)
	z<-matrix(z,full_n,m)
	beta0<-matrix(rep(3,m),m,1)
	u<-runif(full_n,0,1)
	# simulating proportional hazard survival data with exponential baseline survival function
	t<-(0.1)*(-log(u)*c(exp(-z%*%beta0)))


	c<-runif(full_n,0,1)

	delta<-rep(0,full_n)

	for (i in 1:full_n)
	{
		if (t[i]<c[i])
			delta[i]<-1
		else
		{
			delta[i]<-0
			t[i]<-c[i]
		}
	}

	# sorting oberserved data according to accending order of t
	z<-z[order(t),]
	delta<-delta[order(t)]
	t<-t[order(t)]


	m<-dim(z)[2]
	full_n<-length(t)

	zz<-array(0,c(full_n,m,m))


	# distinct t values
	unique_t<-unique(t)
	n<-length(unique_t)
	s<-matrix(0,n,m)
	d<-rep(0,n)
	index<-rep(0,n)





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

	for(i in 1:m)
	{
		for(j in 1:m)
			{
				zz[,i,j]<-z[,i]*z[,j]
			}
	}


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
		
		#negative Hessian matrix for partial likelihood function
		neghessian<-apply(neghessian,c(2,3),sum)

		beta<-beta_old+solve(neghessian+diag(10^(-6),m))%*%gradient
		k<-k+1
	}

	beta_dis<-beta
	n_dis<-n

	# variance-covariance matrix for the beta estimation
	temp1<-exp(z%*%beta_dis)
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
	neghessian<-apply(neghessian,c(2,3),sum)
	# variance-covariance matrix computed here
	varcovar_dis<-solve(neghessian+diag(10^(-6),m))



	# repeated t values

	for(i in 451:500)
		t[i*2]=t[i*2-1]
		
	unique_t<-unique(t)
	n<-length(unique_t)
	s<-matrix(0,n,m)
	d<-rep(0,n)
	index<-rep(0,n)





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

	for(i in 1:m)
	{
		for(j in 1:m)
			{
				zz[,i,j]<-z[,i]*z[,j]
			}
	}


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
		
		#negative Hessian matrix for partial likelihood function
		neghessian<-apply(neghessian,c(2,3),sum)

		beta<-beta_old+solve(neghessian+diag(10^(-6),m))%*%gradient
		k<-k+1
	}

	beta_rep<-beta
	n_rep<-n

	# variance-covariance matrix for the beta estimation
	temp1<-exp(z%*%beta_rep)
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
	neghessian<-apply(neghessian,c(2,3),sum)
	# variance-covariance matrix computed here
	varcovar_rep<-solve(neghessian+diag(10^(-6),m))
    beta_rep
	n_rep
	beta_dis
	n_dis
	para_dis[,rr]<-beta_dis
	para_rep[,rr]<-beta_rep	
	cat("\n\n\nsimulation:  ",rr)
}
library(stats)
t.test(para_dis[1,], para_rep[1,], alternative = "two.sided", paired = T, var.equal = T, conf.level = 0.95)

