# Additional notes

## 5. Potential performance improvements
    - Implement thread-safety for instances where there are many words and many requests.
    - Explore implementing Regex for words with hyphens, apostrophes and other special characters
    - Regex would also improve tokenization, thereby improving searching

## 6. Additional enhancement for project completeness
    - Account for words that come in as plurals
    - An API point that takes a single a word to check if it is sensitive

## Deployment Strategy
    - Build the Microservice as a Docker image, this helps to run it in multiple environments
    - Deploy on a service like OpenShift, it is Docker ready and has built-in security features
    
