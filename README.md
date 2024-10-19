# kob (king of bots)  
y总的springboot基础课的项目代码  
springboot:
    backend port: 3000
    matchingsystem port: 3001
    botrunningsystem port: 3002
Vue port: 8080

**update: 2024/10/19**
**前端有bug，出现了蛇有时可以触碰wall而没有判定失败，以及经常提示前端获取到的snakes为空，应该是前端渲染出问题而后端是正确的，尝试debug失败，实在没有能力修复了，被迫把前端删除然后拿y总的前端代码来测试，果然bug消失了，由此定位问题在前端，希望有能力的人可以来解决它，特此备份**
