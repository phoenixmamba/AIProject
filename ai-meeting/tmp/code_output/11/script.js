// 进制转换器功能实现

// DOM 元素引用
const inputValue = document.getElementById('inputValue');
const fromBase = document.getElementById('fromBase');
const toBase = document.getElementById('toBase');
const convertBtn = document.getElementById('convertBtn');
const resultDisplay = document.getElementById('result');

// 进制转换函数
function convertBase(value, fromBase, toBase) {
    // 验证输入
    if (!value) {
        return '请输入有效的数值';
    }
    
    // 根据源进制转换为十进制
    let decimalValue;
    try {
        decimalValue = parseInt(value, parseInt(fromBase));
        
        if (isNaN(decimalValue)) {
            return '输入数值格式错误';
        }
    } catch (error) {
        return '输入数值格式错误';
    }
    
    // 从十进制转换为目标进制
    let result;
    try {
        result = decimalValue.toString(parseInt(toBase));
        
        // 对十六进制结果进行大写处理
        if (parseInt(toBase) === 16) {
            result = result.toUpperCase();
        }
    } catch (error) {
        return '转换过程中发生错误';
    }
    
    return result;
}

// 输入验证函数
function validateInput(value, base) {
    const baseNum = parseInt(base);
    
    if (!value) {
        return false;
    }
    
    // 根据进制验证字符有效性
    const validChars = {
        2: /^[01]+$/,
        8: /^[0-7]+$/,
        10: /^[0-9]+$/,
        16: /^[0-9A-Fa-f]+$/
    };
    
    return validChars[baseNum].test(value);
}

// 转换按钮点击事件
convertBtn.addEventListener('click', function() {
    const value = inputValue.value.trim();
    const from = fromBase.value;
    const to = toBase.value;
    
    // 验证输入
    if (!validateInput(value, from)) {
        resultDisplay.textContent = '输入数值格式错误，请检查进制类型';
        resultDisplay.style.color = '#ff4444';
        return;
    }
    
    // 执行转换
    const result = convertBase(value, from, to);
    
    // 显示结果
    if (result.includes('错误')) {
        resultDisplay.style.color = '#ff4444';
    } else {
        resultDisplay.style.color = '#00ff88';
    }
    
    resultDisplay.textContent = result;
});

// 输入框回车键支持
inputValue.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        convertBtn.click();
    }
});

// 动态更新输入提示
fromBase.addEventListener('change', function() {
    const base = parseInt(this.value);
    let placeholder = '';
    
    switch (base) {
        case 2:
            placeholder = '请输入二进制数值，如：1010';
            break;
        case 8:
            placeholder = '请输入八进制数值，如：123';
            break;
        case 10:
            placeholder = '请输入十进制数值，如：123';
            break;
        case 16:
            placeholder = '请输入十六进制数值，如：1A3F';
            break;
    }
    
    inputValue.placeholder = placeholder;
});

// 初始化输入提示
fromBase.dispatchEvent(new Event('change'));

// 添加一些动画效果
document.addEventListener('DOMContentLoaded', function() {
    // 页面加载时的淡入效果
    const elements = document.querySelectorAll('.converter-card, .about-content');
    elements.forEach((element, index) => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            element.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            element.style.opacity = '1';
            element.style.transform = 'translateY(0)';
        }, index * 200);
    });
    
    // 为结果显示添加脉冲效果
    setInterval(() => {
        if (resultDisplay.textContent !== '等待输入...' && 
            !resultDisplay.textContent.includes('错误')) {
            resultDisplay.style.textShadow = '0 0 10px rgba(0, 255, 136, 0.7)';
            setTimeout(() => {
                resultDisplay.style.textShadow = 'none';
            }, 500);
        }
    }, 3000);
});

// 错误处理
window.addEventListener('error', function(e) {
    console.error('发生错误:', e.error);
    resultDisplay.textContent = '系统错误，请刷新页面重试';
    resultDisplay.style.color = '#ff4444';
});