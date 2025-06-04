local keys = redis.call('KEYS', 'chat:room:*:*')
local maxCount = 0
local topArticleId = nil
local topCategory = nil

for i, key in ipairs(keys) do
    local count = redis.call('SCARD', key)
    if count > maxCount then
        maxCount = count
        local category, articleId = string.match(key, "chat:room:([^:]+):(.+)")
        topArticleId = articleId
        topCategory = category
    end
end

if topArticleId then
    return {topCategory, topArticleId, maxCount}
else
    return {}
end