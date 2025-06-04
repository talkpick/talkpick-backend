local pattern = ARGV[1]
local keys = redis.call('KEYS', pattern)
local maxCount = 0
local topArticleId = nil

for i, key in ipairs(keys) do
    local count = redis.call('SCARD', key)
    if count > maxCount then
        maxCount = count
        topArticleId = string.match(key, "chat:room:[^:]+:(.+)")
    end
end

if topArticleId then
    return {topArticleId, maxCount}
else
    return {}
end