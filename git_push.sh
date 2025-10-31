#!/bin/bash

# Navigate to your repository directory (optional, if you run the script from the repo root)
# cd /path/to/your/git/repo

# Stage all changes
git add .

# Prompt for a commit message
echo "Enter your commit message:"
read commitMessage

# Commit the changes with the provided message
git commit -m "$commitMessage"

# Prompt for the branch name
echo "Enter the branch name to push to (e.g., main, master, develop):"
read branchName

# Push the changes to the remote repository
git push origin "${branchName:-master}"

echo "Git operations completed: changes added, committed, and pushed to $branchName."