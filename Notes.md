# Additional notes

## 5. Potential performance improvements
    - Implement thread-safety for instances where there are many words and many requests.
    - Explore implementing Regex for words with hyphens, apostrophes and other special characters
    - Regex would also improve tokenization, thereby improving searching

## 6. Additional enhancement for project completeness
    - Account for words that come in as plurals
    - An API point that takes a single a word to check if it is sensitive

## Deployment Strategy
    - Containerization: Package the application into a multi-stage Docker image to guarantee runtime environment parity 
        across local, staging, and production tiers.
    - Container Orchestration: Deploy workloads onto an enterprise Kubernetes platform, such as Red Hat OpenShift to
        leverage automated scaling, native health probing, and seamless rolling updates.
    - Security & Compliance: Utilize OpenShift's built-in governance features, including restrictive Security Context
        Constraints, role-based access control, and automated container vulnerability scanning.
    
