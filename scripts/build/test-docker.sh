#!/bin/bash
set -xe
docker build -t opsera-git-gateway-service:kube-test  ../../
docker run --rm \
        440953937617.dkr.ecr.us-east-2.amazonaws.com/kubectl \
        aws ecr get-login-password \
        --region us-east-2 \
        | docker login --username AWS \
        --password-stdin 440953937617.dkr.ecr.us-east-2.amazonaws.com

date_tag=`date +%-d-%m-%Y-%T | sed 's/:/-/g'`
docker tag opsera-git-gateway-service:kube-test 440953937617.dkr.ecr.us-east-2.amazonaws.com/opsera-git-gateway-service:kube-test
docker tag opsera-git-gateway-service:kube-test 440953937617.dkr.ecr.us-east-2.amazonaws.com/opsera-git-gateway-service:kube-test-${date_tag}

docker push 440953937617.dkr.ecr.us-east-2.amazonaws.com/opsera-git-gateway-service:kube-test
docker push 440953937617.dkr.ecr.us-east-2.amazonaws.com/opsera-git-gateway-service:kube-test-${date_tag}


