# **Complete Guide: AWS Elastic IP & VPC for Beginners**

## **Table of Contents**
1. [Understanding the Problem](#understanding-the-problem)
2. [What is VPC?](#what-is-vpc)
3. [What is Elastic IP?](#what-is-elastic-ip)
4. [How They Work Together](#how-they-work-together)
5. [Step-by-Step Implementation](#step-by-step-implementation)
6. [Cost Considerations](#cost-considerations)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)

---

## **Understanding the Problem**

### **Your Current Situation:**
```
EC2 Instance Lifecycle:
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Running       │    │   Stopped       │    │   Running       │
│ IP: 43.201.54.15│────▶│ IP: None        │────▶│ IP: 52.79.123.45│
└─────────────────┘    └─────────────────┘    └─────────────────┘
     ↑                                              ↑
  Original IP                                  New IP (Problem!)
```

### **The Problem:**
- **GitHub Webhook**: Points to `43.201.54.15:8080/github-webhook/`
- **After Reboot**: IP changes to `52.79.123.45`
- **Result**: Webhook breaks, Jenkins pipeline fails

### **The Solution:**
Use **Elastic IP** - a static IP that stays with your instance forever!

---

## **What is VPC?**

### **VPC (Virtual Private Cloud) - Think of it as Your Digital Office Building**

```
┌─────────────────────────────────────────────────────────────┐
│                    AWS Cloud (The City)                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Your VPC (Your Office Building)        │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │   │
│  │  │   Subnet 1  │  │   Subnet 2  │  │   Subnet 3  │ │   │
│  │  │  (Floor 1)  │  │  (Floor 2)  │  │  (Floor 3)  │ │   │
│  │  │             │  │             │  │             │ │   │
│  │  │ EC2 Instance│  │ RDS Database│  │ Load Balancer│ │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘ │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### **Key VPC Concepts:**

#### **1. Subnets (Floors in Your Building)**
- **Public Subnet**: Has internet access (like a lobby)
- **Private Subnet**: No direct internet (like secure server rooms)

#### **2. Internet Gateway (Building's Main Entrance)**
- Allows your VPC to connect to the internet
- Required for public subnets

#### **3. Security Groups (Building Security)**
- Controls who can enter/exit
- Like firewall rules for your instances

---

## **What is Elastic IP?**

### **Elastic IP - Your Permanent Mailing Address**

Think of Elastic IP like getting a **permanent mailing address** for your business:

```
Traditional EC2 IP (Temporary):
┌─────────────────┐
│ Your Business   │ ← Today: 123 Main St
│                 │ ← Tomorrow: 456 Oak Ave (After moving!)
│                 │ ← Next week: 789 Pine Rd (After moving again!)
└─────────────────┘

Elastic IP (Permanent):
┌─────────────────┐
│ Your Business   │ ← Always: 100 Business Blvd
│                 │ ← Never changes, even if you move offices!
└─────────────────┘
```

### **Benefits:**
1. **Static IP**: Never changes (43.201.54.15 forever!)
2. **DNS Friendly**: You can point your domain to it
3. **Webhook Stable**: Jenkins/GitHub webhooks won't break
4. **Disaster Recovery**: Move between instances easily

---

## **How They Work Together**

### **The Complete Picture:**

```
Internet
    │
    ▼
┌─────────────────────────────────────────────────────────┐
│                      Your VPC                          │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │              Public Subnet                      │   │
│  │                                                 │   │
│  │  ┌─────────────────────────────────────────┐   │   │
│  │  │           Your EC2 Instance              │   │   │
│  │  │                                         │   │   │
│  │  │  Private IP: 10.0.1.100 (Internal)     │   │   │
│  │  │  Elastic IP: 43.201.54.15 (External)   │◄──┼───┼── Your Domain
│  │  │                                         │   │   │    api.yourdomain.com
│  │  │  - Jenkins (Port 8080)                  │   │   │
│  │  │  - Your App (Port 8080)                 │   │   │
│  │  │  - SSH (Port 22)                        │   │   │
│  │  └─────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

---

## **Step-by-Step Implementation**

### **Prerequisites:**
- AWS Console access
- Running EC2 instance
- Basic understanding of your instance ID

### **Step 1: Allocate an Elastic IP**

#### **Method A: AWS Console (Recommended for beginners)**

1. **Login to AWS Console**
   - Go to [AWS Console](https://console.aws.amazon.com)
   - Navigate to **EC2 Dashboard**

2. **Go to Elastic IPs**
   ```
   EC2 Dashboard → Network & Security → Elastic IPs
   ```

3. **Allocate New Elastic IP**
   - Click **"Allocate Elastic IP address"**
   - **Network Border Group**: Select your region (e.g., `us-east-1`)
   - **Public IPv4 address pool**: Amazon's pool of IPv4 addresses
   - Click **"Allocate"**

4. **Record Your Elastic IP**
   ```
   Example Result:
   Allocation ID: eipalloc-12345678901234567
   Public IP: 43.201.54.15
   ```

#### **Method B: AWS CLI**
```bash
# Install AWS CLI first
pip install awscli

# Configure credentials
aws configure

# Allocate Elastic IP
aws ec2 allocate-address --domain vpc --region us-east-1

# Example Output:
{
    "PublicIp": "43.201.54.15",
    "AllocationId": "eipalloc-12345678901234567",
    "Domain": "vpc"
}
```

### **Step 2: Find Your Instance Information**

#### **Get Instance ID:**
1. **AWS Console**: EC2 Dashboard → Instances
2. **Find your instance**: Look for the one running your Jenkins/App
3. **Note the Instance ID**: Example: `i-0123456789abcdef0`

#### **CLI Method:**
```bash
# List all instances
aws ec2 describe-instances --query 'Reservations[*].Instances[*].[InstanceId,State.Name,PublicIpAddress,Tags[?Key==`Name`].Value|[0]]' --output table

# Example Output:
|  i-0123456789abcdef0  |  running  |  43.201.54.15  |  MyJenkinsServer  |
```

### **Step 3: Associate Elastic IP with Your Instance**

#### **Method A: AWS Console**

1. **Select Your Elastic IP**
   - Go back to **Elastic IPs** page
   - Select the IP you just allocated

2. **Associate with Instance**
   - Click **"Actions"** → **"Associate Elastic IP address"**
   - **Resource type**: Instance
   - **Instance**: Select your Jenkins/App instance
   - **Private IP**: Leave as default
   - **Allow reassociation**: Check this box (important!)
   - Click **"Associate"**

3. **Verify Association**
   ```
   Status should show:
   ✅ Associated with instance i-0123456789abcdef0
   ```

#### **Method B: AWS CLI**
```bash
# Associate Elastic IP with instance
aws ec2 associate-address \
    --instance-id i-0123456789abcdef0 \
    --allocation-id eipalloc-12345678901234567

# Verify association
aws ec2 describe-addresses --allocation-ids eipalloc-12345678901234567
```

### **Step 4: Update Security Groups**

#### **Check Current Security Group:**
1. **Go to Instance Details**
2. **Security Tab** → Note Security Group ID (e.g., `sg-12345678`)

#### **Update Security Group Rules:**
```bash
# Allow SSH (Port 22)
aws ec2 authorize-security-group-ingress \
    --group-id sg-12345678 \
    --protocol tcp \
    --port 22 \
    --cidr 0.0.0.0/0

# Allow Jenkins/App (Port 8080)
aws ec2 authorize-security-group-ingress \
    --group-id sg-12345678 \
    --protocol tcp \
    --port 8080 \
    --cidr 0.0.0.0/0

# Allow HTTPS (Port 443) - for future SSL
aws ec2 authorize-security-group-ingress \
    --group-id sg-12345678 \
    --protocol tcp \
    --port 443 \
    --cidr 0.0.0.0/0
```

### **Step 5: Test the Connection**

#### **Test SSH:**
```bash
# Your old command (might not work)
ssh -i your-key.pem ec2-user@OLD_CHANGING_IP

# Your new command (will always work)
ssh -i your-key.pem ec2-user@43.201.54.15
```

#### **Test Web Access:**
```bash
# Test Jenkins/App
curl http://43.201.54.15:8080

# Or open in browser
http://43.201.54.15:8080
```

### **Step 6: Update Your Domain (GoDaddy)**

#### **DNS Configuration:**
1. **Login to GoDaddy**
2. **Go to DNS Management** for your domain
3. **Update A Records:**

```
Type: A
Name: @ (for root domain)
Value: 43.201.54.15
TTL: 1 Hour

Type: A  
Name: api (for api.yourdomain.com)
Value: 43.201.54.15
TTL: 1 Hour
```

#### **Verify DNS Propagation:**
```bash
# Check if DNS is working
nslookup api.yourdomain.com
# Should return: 43.201.54.15

# Alternative check
dig api.yourdomain.com
```

### **Step 7: Update Jenkins Webhook**

#### **In GitHub Repository:**
1. **Go to Settings** → **Webhooks**
2. **Edit existing webhook**
3. **Update Payload URL:**
   ```
   Old: http://43.201.54.15:8080/github-webhook/
   New: http://api.yourdomain.com:8080/github-webhook/
   ```
4. **Test webhook** - should work now!

---

## **Cost Considerations**

### **Elastic IP Pricing:**

| **Scenario** | **Cost** | **Explanation** |
|--------------|----------|-----------------|
| **Associated with running instance** | **FREE** | No charge when actively used |
| **Associated with stopped instance** | **$0.005/hour** | ~$3.60/month to prevent waste |
| **Unassociated (allocated but unused)** | **$0.005/hour** | ~$3.60/month to prevent waste |

### **Cost Optimization Tips:**
```bash
# ✅ Good Practice: Release unused Elastic IPs
aws ec2 release-address --allocation-id eipalloc-12345678

# ✅ Good Practice: Only allocate what you need
# ❌ Bad Practice: Allocating multiple IPs "just in case"
```

### **Example Monthly Costs:**
```
Scenario 1: Development Server (8 hours/day)
- Running time: 240 hours/month
- Stopped time: 480 hours/month
- Cost: (480 × $0.005) = $2.40/month

Scenario 2: Production Server (24/7)
- Running time: 720 hours/month  
- Stopped time: 0 hours/month
- Cost: $0.00/month

Recommendation: Use Elastic IP for production, regular IP for development
```

---

## **Best Practices**

### **1. Naming and Tagging**
```bash
# Tag your Elastic IP for easy identification
aws ec2 create-tags \
    --resources eipalloc-12345678 \
    --tags Key=Name,Value="Jenkins-Production-IP" \
           Key=Environment,Value="Production" \
           Key=Project,Value="YourProject"
```

### **2. Security Group Configuration**
```bash
# ✅ Restrictive SSH access (your IP only)
aws ec2 authorize-security-group-ingress \
    --group-id sg-12345678 \
    --protocol tcp \
    --port 22 \
    --cidr YOUR_IP/32

# ✅ Public web access
aws ec2 authorize-security-group-ingress \
    --group-id sg-12345678 \
    --protocol tcp \
    --port 8080 \
    --cidr 0.0.0.0/0
```

### **3. Backup Strategy**
```bash
# Create AMI snapshot before major changes
aws ec2 create-image \
    --instance-id i-0123456789abcdef0 \
    --name "Jenkins-Backup-$(date +%Y%m%d)" \
    --description "Pre-Elastic-IP-setup backup"
```

### **4. Documentation**
```yaml
# Keep a record in your project
infrastructure:
  elastic_ip: 43.201.54.15
  allocation_id: eipalloc-12345678
  instance_id: i-0123456789abcdef0
  domain: api.yourdomain.com
  jenkins_url: http://api.yourdomain.com:8080
  ssh_command: ssh -i key.pem ec2-user@43.201.54.15
```

---

## **Troubleshooting**

### **Problem 1: Can't SSH after Elastic IP association**

#### **Symptoms:**
```bash
ssh: connect to host 43.201.54.15 port 22: Connection refused
```

#### **Solution:**
```bash
# Check security group
aws ec2 describe-security-groups --group-ids sg-12345678

# Add SSH rule if missing
aws ec2 authorize-security-group-ingress \
    --group-id sg-12345678 \
    --protocol tcp \
    --port 22 \
    --cidr 0.0.0.0/0
```

### **Problem 2: Elastic IP shows as "associated" but connection fails**

#### **Check Association Status:**
```bash
aws ec2 describe-addresses --allocation-ids eipalloc-12345678
```

#### **Re-associate if needed:**
```bash
aws ec2 disassociate-address --allocation-id eipalloc-12345678
aws ec2 associate-address \
    --instance-id i-0123456789abcdef0 \
    --allocation-id eipalloc-12345678 \
    --allow-reassociation
```

### **Problem 3: Domain not pointing to Elastic IP**

#### **Check DNS:**
```bash
# Check current DNS resolution
nslookup api.yourdomain.com

# Check DNS propagation globally
https://www.whatsmydns.net/#A/api.yourdomain.com
```

#### **Common DNS Issues:**
- **TTL too high**: Reduce to 300 seconds (5 minutes)
- **Wrong record type**: Use 'A' record, not 'CNAME'
- **Propagation delay**: Wait 24-48 hours for global propagation

### **Problem 4: Jenkins webhook still failing**

#### **Test webhook manually:**
```bash
# Test if Jenkins is reachable
curl -X POST http://43.201.54.15:8080/github-webhook/

# Test with domain
curl -X POST http://api.yourdomain.com:8080/github-webhook/

# Check Jenkins logs
sudo journalctl -u jenkins -f
```

### **Problem 5: Instance stopped, now Elastic IP costs money**

#### **Check billing:**
```bash
# See all Elastic IPs and their status
aws ec2 describe-addresses
```

#### **Start instance to avoid charges:**
```bash
aws ec2 start-instances --instance-ids i-0123456789abcdef0
```

---

## **Quick Reference Commands**

### **Management Commands:**
```bash
# List all Elastic IPs
aws ec2 describe-addresses

# Associate Elastic IP
aws ec2 associate-address --instance-id INSTANCE_ID --allocation-id ALLOCATION_ID

# Disassociate Elastic IP  
aws ec2 disassociate-address --allocation-id ALLOCATION_ID

# Release Elastic IP (permanent - be careful!)
aws ec2 release-address --allocation-id ALLOCATION_ID

# Check instance status
aws ec2 describe-instances --instance-ids INSTANCE_ID
```

### **Testing Commands:**
```bash
# Test SSH
ssh -i your-key.pem ec2-user@YOUR_ELASTIC_IP

# Test web service
curl http://YOUR_ELASTIC_IP:8080

# Test DNS resolution
nslookup api.yourdomain.com
```

---

## **Summary**

After completing this guide, you'll have:

✅ **Static IP Address**: `43.201.54.15` that never changes  
✅ **Stable Jenkins Webhooks**: GitHub → Jenkins pipeline always works  
✅ **Domain Integration**: `api.yourdomain.com` points to your server  
✅ **Cost Optimization**: Free when instance is running  
✅ **Disaster Recovery**: Can move IP between instances  

Your final setup:
```
GitHub Webhook: http://api.yourdomain.com:8080/github-webhook/
SSH Access: ssh -i key.pem ec2-user@43.201.54.15
Web Access: http://api.yourdomain.com:8080
Domain: api.yourdomain.com → 43.201.54.15 (Elastic IP)
```

**No more broken webhooks after EC2 reboots! 🎉** 