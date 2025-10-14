// 导航菜单切换功能
document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.querySelector('.hamburger');
    const navMenu = document.querySelector('.nav-menu');
    
    // 切换移动端菜单显示/隐藏
    hamburger.addEventListener('click', function() {
        navMenu.classList.toggle('active');
    });
    
    // 平滑滚动到锚点
    const navLinks = document.querySelectorAll('.nav-menu a');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            const targetSection = document.querySelector(targetId);
            
            if (targetSection) {
                // 关闭移动端菜单（如果打开）
                navMenu.classList.remove('active');
                
                // 平滑滚动到目标区域
                window.scrollTo({
                    top: targetSection.offsetTop - 80,
                    behavior: 'smooth'
                });
            }
        });
    });
    
    // 表单提交处理
    const contactForm = document.querySelector('.contact-form');
    
    contactForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // 获取表单数据
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const message = document.getElementById('message').value;
        
        // 简单验证
        if (name && email && message) {
            // 在实际应用中，这里会发送数据到服务器
            alert('感谢您的留言！我们会尽快回复。');
            contactForm.reset();
        } else {
            alert('请填写所有必填字段。');
        }
    });
    
    // CTA按钮点击事件
    const ctaButton = document.querySelector('.cta-button');
    
    ctaButton.addEventListener('click', function() {
        // 滚动到关于我们区域
        const aboutSection = document.querySelector('#about');
        
        if (aboutSection) {
            window.scrollTo({
                top: aboutSection.offsetTop - 80,
                behavior: 'smooth'
            });
        }
    });
});